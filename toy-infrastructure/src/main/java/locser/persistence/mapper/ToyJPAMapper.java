package locser.persistence.mapper;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import locser.toy.domain.model.entity.Toy;

/**
 * JPA Repository interface for Toy entity.
 */
@Repository
public interface ToyJPAMapper extends JpaRepository<Toy, Long>, JpaSpecificationExecutor<Toy> {

        /**
         * Find a toy by its ID.
         *
         * @param id The ID of the toy
         * @return An Optional containing the toy if found, or empty if not found
         */
        Optional<Toy> findOneById(Long id);

        /**
         * Find toys by user ID.
         *
         * @param userId The user ID
         * @return List of toys belonging to the user
         */
        List<Toy> findByUserId(Long userId);

        /**
         * Find toys by user ID with sorting.
         *
         * @param userId The user ID
         * @param sort   Sort specification
         * @return List of toys belonging to the user
         */
        List<Toy> findByUserId(Long userId, Sort sort);

        /**
         * Find toys by user ID with pagination.
         *
         * @param userId   The user ID
         * @param pageable Pagination information
         * @return Page of toys belonging to the user
         */
        Page<Toy> findByUserId(Long userId, Pageable pageable);

        /**
         * Find toys by status.
         *
         * @param status The toy status
         * @return List of toys with the given status
         */
        List<Toy> findByStatus(Integer status);

        /**
         * Find toys by status with sorting.
         *
         * @param status The toy status
         * @param sort   Sort specification
         * @return List of toys with the given status
         */
        List<Toy> findByStatus(Integer status, Sort sort);

        /**
         * Find toys by status with pagination.
         *
         * @param status   The toy status
         * @param pageable Pagination information
         * @return Page of toys with the given status
         */
        Page<Toy> findByStatus(Integer status, Pageable pageable);

        /**
         * Find toys by user ID and status.
         *
         * @param userId The user ID
         * @param status The toy status
         * @return List of toys belonging to the user with the given status
         */
        List<Toy> findByUserIdAndStatus(Long userId, Integer status);

        /**
         * Find toys by user ID and status with sorting.
         *
         * @param userId The user ID
         * @param status The toy status
         * @param sort   Sort specification
         * @return List of toys belonging to the user with the given status
         */
        List<Toy> findByUserIdAndStatus(Long userId, Integer status, Sort sort);

        /**
         * Find toys by user ID and status with pagination.
         *
         * @param userId   The user ID
         * @param status   The toy status
         * @param pageable Pagination information
         * @return Page of toys belonging to the user with the given status
         */
        Page<Toy> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);

        /**
         * Find toys by campaign ID.
         *
         * @param campaignId The campaign ID
         * @return List of toys in the given campaign
         */
        List<Toy> findByCampaignId(Long campaignId);

        /**
         * Find toys by campaign ID with sorting.
         *
         * @param campaignId The campaign ID
         * @param sort       Sort specification
         * @return List of toys in the given campaign
         */
        List<Toy> findByCampaignId(Long campaignId, Sort sort);

        /**
         * Find toys by campaign ID with pagination.
         *
         * @param campaignId The campaign ID
         * @param pageable   Pagination information
         * @return Page of toys in the given campaign
         */
        Page<Toy> findByCampaignId(Long campaignId, Pageable pageable);

        /**
         * Find toys by user ID and campaign ID.
         *
         * @param userId     The user ID
         * @param campaignId The campaign ID
         * @return List of toys belonging to the user in the given campaign
         */
        List<Toy> findByUserIdAndCampaignId(Long userId, Long campaignId);

        /**
         * Find toys by user ID and campaign ID with sorting.
         *
         * @param userId     The user ID
         * @param campaignId The campaign ID
         * @param sort       Sort specification
         * @return List of toys belonging to the user in the given campaign
         */
        List<Toy> findByUserIdAndCampaignId(Long userId, Long campaignId, Sort sort);

        /**
         * Find toys by user ID and campaign ID with pagination.
         *
         * @param userId     The user ID
         * @param campaignId The campaign ID
         * @param pageable   Pagination information
         * @return Page of toys belonging to the user in the given campaign
         */
        Page<Toy> findByUserIdAndCampaignId(Long userId, Long campaignId, Pageable pageable);

        /**
         * Count toys by user ID.
         *
         * @param userId The user ID
         * @return Number of toys belonging to the user
         */
        long countByUserId(Long userId);

        /**
         * Count toys by status.
         *
         * @param status The toy status
         * @return Number of toys with the given status
         */
        long countByStatus(Integer status);

        /**
         * Count toys by user ID and status.
         *
         * @param userId The user ID
         * @param status The toy status
         * @return Number of toys belonging to the user with the given status
         */
        long countByUserIdAndStatus(Long userId, Integer status);

        /**
         * Count toys by campaign ID.
         *
         * @param campaignId The campaign ID
         * @return Number of toys in the given campaign
         */
        long countByCampaignId(Long campaignId);

        /**
         * Count toys by user ID and campaign ID.
         *
         * @param userId     The user ID
         * @param campaignId The campaign ID
         * @return Number of toys belonging to the user in the given campaign
         */
        long countByUserIdAndCampaignId(Long userId, Long campaignId);

        /**
         * Find toys by list of IDs.
         *
         * @param ids List of toy IDs
         * @return List of toys with the given IDs
         */
        List<Toy> findByIdIn(List<Long> ids);

        /**
         * Find toys by list of IDs and status.
         *
         * @param ids    List of toy IDs
         * @param status Toy status
         * @return List of toys with the given IDs and status
         */
        List<Toy> findByIdInAndStatus(List<Long> ids, int status);

        /**
         * Update toys status and campaign ID by list of IDs.
         *
         * @param toyIds     List of toy IDs
         * @param campaignId Campaign ID
         * @param status     New status
         * @return Number of toys updated
         */
        @Modifying
        @Transactional
        @Query("UPDATE Toy t SET t.status = :status, t.campaignId = :campaignId WHERE t.id IN :toyIds")
        int updateStatusAndCampaignIdByIds(List<Long> toyIds, Long campaignId, int status);

        int countByCampaignIdAndStatus(Long campaignId, Integer status);

        List<Toy> findByCampaignIdAndStatus(Long campaignId, Integer status);

        /**
         * Get a random available toy from a campaign using native SQL for better
         * performance.
         * This uses RAND() function which is more efficient than loading all toys.
         *
         * @param campaignId The campaign ID
         * @param status     The toy status
         * @return A random available toy, or null if none available
         */
        @Query(value = "SELECT * FROM toys t " +
                        "WHERE t.campaign_id = :campaignId " +
                        "AND t.status = :status " +
                        "ORDER BY RAND() " +
                        "LIMIT 1", nativeQuery = true)
        Toy findRandomAvailableToyInCampaign(
                        @Param("campaignId") Long campaignId,
                        @Param("status") Integer status);

        @Modifying
        @Query("UPDATE Toy t SET t.status = :status WHERE t.id = :toyId")
        void updateStatus(Long toyId, Integer status);

}
