package locser.infrastructure.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Repository;

import locser.infrastructure.cache.RedisGiveawayCampaignCache;
import locser.toy.domain.exception.BadRequestException;
import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.entity.Toy;
import locser.toy.domain.model.entity.ToyParticipation;
import locser.toy.domain.model.enums.ToyStatus;
import locser.toy.domain.repository.EventRepository;
import locser.toy.domain.repository.GiveawayCampaignRepository;
import locser.toy.domain.repository.ToyParticipationRepository;
import locser.toy.domain.repository.ToyRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GiveawayCampaignRepositoryImpl implements GiveawayCampaignRepository {
    private final EventRepository eventRepository;
    private final ToyRepository toyRepository;
    private final ToyParticipationRepository toyParticipationRepository;
    private final RedisGiveawayCampaignCache redisCache;

    @Override
    public Optional<Event> findById(Long id) {
        // Try Redis first
        Object cachedCampaign = redisCache.getCachedCampaign(id);
        if (cachedCampaign != null) {
            return Optional.of((Event) cachedCampaign);
        }

        // If not in Redis, get from database
        Optional<Event> campaign = eventRepository.findOneById(id);
        campaign.ifPresent(c -> redisCache.cacheCampaign(id, c));
        return campaign;
    }

    @Override
    public Event save(Event campaign) {
        Event savedCampaign = eventRepository.save(campaign);
        redisCache.cacheCampaign(campaign.getId(), savedCampaign);
        return savedCampaign;
    }

    @Override
    public List<Toy> findAvailableToys(Long campaignId) {
        // Try Redis first
        long availableCount = redisCache.getAvailableToysCount(campaignId);
        if (availableCount > 0) {
            // Get random toy from Redis using atomic operation
            Optional<Long> randomToyId = redisCache.getRandomAvailableToy(campaignId);
            if (randomToyId.isPresent()) {
                return toyRepository.findByIdIn(List.of(randomToyId.get()));
            }
        }

        // If not in Redis or no toys available, get from database
        List<Toy> toys = toyRepository.findByCampaignIdAndStatus(
                campaignId,
                ToyStatus.GIVEAWAY_AVAILABLE.getValue());

        // Cache available toys
        Set<Long> toyIds = new HashSet<>(toys.stream().map(Toy::getId).toList());
        redisCache.cacheAvailableToys(campaignId, toyIds);
        return toys;
    }

    @Override
    public boolean hasUserParticipated(Long userId, Long campaignId) {
        // Try Redis first
        if (redisCache.hasUserParticipated(userId, campaignId)) {
            return true;
        }

        // If not in Redis, check database
        boolean participated = toyParticipationRepository.existsByUserIdAndCampaignId(userId, campaignId);
        if (participated) {
            redisCache.markUserParticipated(userId, campaignId);
        }
        return participated;
    }

    @Override
    public ToyParticipation saveParticipation(ToyParticipation participation) {
        // Save participation record
        ToyParticipation savedParticipation = toyParticipationRepository.save(participation);

        // Update toy status
        Toy toy = toyRepository.findOneById(participation.getToyId())
                .orElseThrow(() -> new BadRequestException("Toy not found"));
        toy.setStatus(ToyStatus.GIVEAWAY_CLAIMED.getValue());
        toyRepository.save(toy);

        // Update available toys count
        updateAvailableToys(participation.getCampaignId(), -1);

        // Mark user as participated in Redis
        redisCache.markUserParticipated(participation.getUserId(), participation.getCampaignId());

        return savedParticipation;
    }

    @Override
    public void updateAvailableToys(Long campaignId, int count) {
        Optional<Event> campaignOpt = findById(campaignId);
        if (campaignOpt.isPresent()) {
            Event campaign = campaignOpt.get();
            campaign.setAvailableToys(campaign.getAvailableToys() + count);
            save(campaign);
        }
    }

    @Override
    public void markUserParticipated(Long userId, Long campaignId) {
        redisCache.markUserParticipated(userId, campaignId);
    }
}