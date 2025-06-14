package locser.toy.domain.repository;

import java.util.List;
import java.util.Optional;

import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.entity.Toy;
import locser.toy.domain.model.entity.ToyParticipation;

public interface GiveawayCampaignRepository {
    Optional<Event> findById(Long id);

    Event save(Event campaign);

    List<Toy> findAvailableToys(Long campaignId);

    boolean hasUserParticipated(Long userId, Long campaignId);

    ToyParticipation saveParticipation(ToyParticipation participation);

    void updateAvailableToys(Long campaignId, int count);

    void markUserParticipated(Long userId, Long campaignId);
}