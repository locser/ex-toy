package locser.toy.domain.repository;

import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.entity.Toy;
import locser.toy.domain.model.entity.ToyParticipation;

public interface GiveawayCampaignRepository {
    Event findById(Long id);

    Event save(Event campaign);

    // List<Toy> findAvailableToys(Long campaignId);

    Toy findAvailableToy(Long campaignId);

    boolean hasUserParticipated(Long userId, Long campaignId);

    ToyParticipation saveParticipation(ToyParticipation participation);

    void updateAvailableToys(Long campaignId, int count);

    void markUserParticipated(Long userId, Long campaignId);

    void decrementAvailableToys(Long campaignId, int count);
}