package locser.persistence.mapper;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import locser.toy.domain.model.entity.ToyParticipation;

/**
 * JPA Repository interface for ToyParticipation entity.
 */
@Repository
public interface ToyParticipationJPAMapper extends JpaRepository<ToyParticipation, Long> {

    /**
     * Find a toy participation by its ID.
     *
     * @param id The ID of the toy participation
     * @return An Optional containing the toy participation if found, or empty if
     *         not found
     */
    Optional<ToyParticipation> findOneById(Long id);

    /**
     * Check if a user has already participated in a campaign.
     *
     * @param userId     The user ID
     * @param campaignId The campaign ID
     * @return true if user has participated, false otherwise
     */
    boolean existsByUserIdAndCampaignId(Long userId, Long campaignId);

    /**
     * Find a toy participation by user ID and campaign ID.
     *
     * @param userId     The user ID
     * @param campaignId The campaign ID
     * @return An Optional containing the toy participation if found
     */
    Optional<ToyParticipation> findByUserIdAndCampaignId(Long userId, Long campaignId);

    /**
     * Find toy participations by user ID.
     *
     * @param userId The user ID
     * @return List of toy participations for the user
     */
    List<ToyParticipation> findByUserId(Long userId);

    /**
     * Find toy participations by user ID with pagination.
     *
     * @param userId   The user ID
     * @param pageable Pagination information
     * @return Page of toy participations for the user
     */
    Page<ToyParticipation> findByUserId(Long userId, Pageable pageable);

    /**
     * Find toy participations by campaign ID.
     *
     * @param campaignId The campaign ID
     * @return List of toy participations for the campaign
     */
    List<ToyParticipation> findByCampaignId(Long campaignId);

    /**
     * Count toy participations by campaign ID.
     *
     * @param campaignId The campaign ID
     * @return Number of participations in the campaign
     */
    long countByCampaignId(Long campaignId);

    /**
     * Count toy participations by user ID.
     *
     * @param userId The user ID
     * @return Number of participations by the user
     */
    long countByUserId(Long userId);

    /**
     * Find toy participations by user ID and status.
     *
     * @param userId The user ID
     * @param status The participation status
     * @return List of toy participations matching the criteria
     */
    List<ToyParticipation> findByUserIdAndStatus(Long userId, Integer status);

    /**
     * Find toy participations by campaign ID and status.
     *
     * @param campaignId The campaign ID
     * @param status     The participation status
     * @return List of toy participations matching the criteria
     */
    List<ToyParticipation> findByCampaignIdAndStatus(Long campaignId, Integer status);
}