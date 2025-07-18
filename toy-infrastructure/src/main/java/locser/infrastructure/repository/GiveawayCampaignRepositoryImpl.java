package locser.infrastructure.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import locser.infrastructure.cache.RedisGiveawayCampaignCache;
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
    public Event findById(Long id) {
        // Try Redis first
        Event cachedCampaign = redisCache.getCachedCampaign(id);
        if (cachedCampaign != null) {
            return cachedCampaign;
        }

        // If not in Redis, get from database
        Event campaign = eventRepository.findOneById(id).orElse(null);

        redisCache.cacheCampaign(id, campaign);
        return campaign;
    }

    @Override
    public Event save(Event campaign) {
        Event savedCampaign = eventRepository.save(campaign);
        redisCache.cacheCampaign(savedCampaign.getId(), savedCampaign);
        return savedCampaign;
    }

    @Override
    public Toy findAvailableToy(Long campaignId) {
        // Try Redis first
        long availableCount = redisCache.getAvailableToysCount(campaignId);
        if (availableCount > 0) {
            Long randomToyId = redisCache.getRandomAvailableToy(campaignId);
            if (randomToyId != null) {
                Toy toy = toyRepository.findOneById(randomToyId).orElse(null);
                if (toy != null && toy.getStatus().equals(ToyStatus.GIVEAWAY_AVAILABLE.getValue())) {
                    return toy;
                }
            }
        }

        // Get from database and refresh cache
        List<Toy> toys = toyRepository.findByCampaignIdAndStatus(
                campaignId,
                ToyStatus.GIVEAWAY_AVAILABLE.getValue());

        if (toys.isEmpty()) {
            return null;
        }

        // Update cache with fresh data
        Set<Long> toyIds = new HashSet<>(toys.stream().map(Toy::getId).toList());
        redisCache.cacheAvailableToys(campaignId, toyIds);

        // Get random toy
        Long randomToyId = redisCache.getRandomAvailableToy(campaignId);
        return randomToyId != null ? toyRepository.findOneById(randomToyId).orElse(null) : null;
    }

    // TODO: Implement this method
    @Override
    public boolean hasUserParticipated(Long userId, Long campaignId) {

        return false;
        // Try Redis first
        // if (redisCache.hasUserParticipated(userId, campaignId)) {
        // return true;
        // }

        // // If not in Redis, check database
        // boolean participated =
        // toyParticipationRepository.existsByUserIdAndCampaignId(userId, campaignId);
        // if (participated) {
        // redisCache.markUserParticipated(userId, campaignId);
        // }
        // return participated;
    }

    @Override
    public ToyParticipation saveParticipation(ToyParticipation participation) {
        // Save participation record
        ToyParticipation savedParticipation = toyParticipationRepository.save(participation);
        // Mark user as participated in Redis
        redisCache.markUserParticipated(participation.getUserId(), participation.getCampaignId());

        // Update toy status

        toyRepository.updateStatus(participation.getToyId(), ToyStatus.GIVEAWAY_CLAIMED.getValue());

        // Update available toys count
        updateAvailableToys(participation.getCampaignId(), -1);

        return savedParticipation;
    }

    @Override
    public void updateAvailableToys(Long campaignId, int count) {
        decrementAvailableToys(campaignId, count);
        // Event campaign = findById(campaignId);
        // if (campaign != null) {
        // campaign.setAvailableToys(campaign.getAvailableToys() + count);
        // save(campaign);
        // }
    }

    @Override
    public void markUserParticipated(Long userId, Long campaignId) {
        redisCache.markUserParticipated(userId, campaignId);
    }

    @Override
    public void decrementAvailableToys(Long campaignId, int count) {
        eventRepository.decrementAvailableToys(campaignId, count);
    }

}