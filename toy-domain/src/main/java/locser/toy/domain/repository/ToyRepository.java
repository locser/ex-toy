package locser.toy.domain.repository;

import java.util.List;
import java.util.Optional;
import locser.toy.domain.model.entity.Toy;

/**
 * Repository interface for Toy entity.
 */
public interface ToyRepository {

  /**
   * Find a toy by its ID.
   *
   * @param id The ID of the toy
   * @return An Optional containing the toy if found, or empty if not found
   */
  Optional<Toy> findOneById(Long id);

  /**
   * Save a toy entity.
   *
   * @param toy The toy to save
   * @return The saved toy
   */
  Toy save(Toy toy);

  /**
   * Find a toy by its ID.
   *
   * @param id The ID of the toy
   * @return The toy, or null if not found
   */
  Toy findById(Long id);

  /**
   * Find toys by user ID.
   *
   * @param userId The user ID
   * @return List of toys belonging to the user
   */
  List<Toy> findByUserId(Long userId);

  /**
   * Find toys by status.
   *
   * @param status The toy status
   * @return List of toys with the given status
   */
  List<Toy> findByStatus(int status);

  /**
   * Find toys by user ID and status.
   *
   * @param userId The user ID
   * @param status The toy status
   * @return List of toys belonging to the user with the given status
   */
  List<Toy> findByUserIdAndStatus(Long userId, int status);

  /**
   * Find toys by campaign ID.
   *
   * @param campaignId The campaign ID
   * @return List of toys in the given campaign
   */
  List<Toy> findByCampaignId(Long campaignId);

  /**
   * Find toys by user ID and campaign ID.
   *
   * @param userId     The user ID
   * @param campaignId The campaign ID
   * @return List of toys belonging to the user in the given campaign
   */
  List<Toy> findByUserIdAndCampaignId(Long userId, Long campaignId);

  /**
   * Find all toys.
   *
   * @return List of all toys
   */
  List<Toy> findAll();

  /**
   * Find all toys with sorting.
   *
   * @param sortBy        Field to sort by
   * @param sortDirection Sort direction (asc, desc)
   * @return Sorted list of all toys
   */
  List<Toy> findAll(String sortBy, String sortDirection);

  /**
   * Find toys with pagination.
   *
   * @param page          Page number (starting from 0)
   * @param size          Page size
   * @param userId        User ID (optional)
   * @param status        Toy status (optional)
   * @param campaignId    Campaign ID (optional)
   * @param sortBy        Field to sort by
   * @param sortDirection Sort direction (asc, desc)
   * @return List of toys for the given page
   */
  List<Toy> findWithPagination(int page, int size, Long userId, Integer status, Long campaignId,
      String sortBy,
      String sortDirection);

  /**
   * Count the total number of toys.
   *
   * @param userId     User ID (optional)
   * @param status     Toy status (optional)
   * @param campaignId Campaign ID (optional)
   * @return Total number of toys
   */
  long count(Long userId, Integer status, Long campaignId);

  /**
   * Find toys by campaign ID and status.
   *
   * @param campaignId Campaign ID
   * @param status     Toy status
   * @return List of toys in the campaign with the given status
   */
  List<Toy> findByCampaignIdAndStatus(Long campaignId, Integer status);

  /**
   * Count toys by campaign ID and status.
   *
   * @param campaignId Campaign ID
   * @param status     Toy status
   * @return Number of toys in the campaign with the given status
   */
  long countByCampaignIdAndStatus(Long campaignId, Integer status);

  /**
   * Batch update toys status by campaign ID.
   *
   * @param campaignId Campaign ID
   * @param oldStatus  Current status
   * @param newStatus  New status to set
   * @return Number of toys updated
   */
  int updateStatusByCampaignId(Long campaignId, Integer oldStatus, Integer newStatus);

  /**
   * Batch update toys to add them to a campaign.
   *
   * @param toyIds     List of toy IDs
   * @param campaignId Campaign ID to assign
   * @param newStatus  New status to set
   * @return Number of toys updated
   */
  int addToysToCampaign(List<Long> toyIds, Long campaignId, Integer newStatus);
}