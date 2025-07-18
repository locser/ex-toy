package locser.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import locser.persistence.mapper.ToyJPAMapper;
import locser.toy.domain.model.entity.Toy;
import locser.toy.domain.model.enums.ToyStatus;
import locser.toy.domain.repository.ToyRepository;

/**
 * Implementation of ToyRepository using JPA.
 */
@Service
public class ToyInfrasRepositoryImpl implements ToyRepository {

  private static final long ALL_RECORDS = -1L;
  private final ToyJPAMapper toyJPAMapper;

  public ToyInfrasRepositoryImpl(ToyJPAMapper toyJPAMapper) {
    this.toyJPAMapper = toyJPAMapper;
  }

  @Override
  public Optional<Toy> findOneById(Long id) {
    return toyJPAMapper.findOneById(id);
  }

  @Override
  public Toy save(Toy toy) {
    return toyJPAMapper.save(toy);
  }

  @Override
  public Toy findById(Long id) {
    return toyJPAMapper.findById(id).orElse(null);
  }

  @Override
  public List<Toy> findByUserId(Long userId) {
    return toyJPAMapper.findByUserId(userId);
  }

  @Override
  public List<Toy> findByStatus(int status) {
    return toyJPAMapper.findByStatus(status);
  }

  @Override
  public List<Toy> findByUserIdAndStatus(Long userId, int status) {
    return toyJPAMapper.findByUserIdAndStatus(userId, status);
  }

  @Override
  public List<Toy> findByCampaignId(Long campaignId) {
    return toyJPAMapper.findByCampaignId(campaignId);
  }

  @Override
  public List<Toy> findByUserIdAndCampaignId(Long userId, Long campaignId) {
    return toyJPAMapper.findByUserIdAndCampaignId(userId, campaignId);
  }

  @Override
  public List<Toy> findAll() {
    return toyJPAMapper.findAll();
  }

  @Override
  public List<Toy> findAll(String sortBy, String sortDirection) {
    Sort sort = createSort(sortBy, sortDirection);
    return toyJPAMapper.findAll(sort);
  }

  @Override
  public List<Toy> findWithPagination(int page, int size, Long userId, Integer status,
      Long campaignId, String sortBy, String sortDirection) {
    Sort sort = createSort(sortBy, sortDirection);
    Pageable pageable = PageRequest.of(page, size, sort);

    // Convert special value to null for filtering
    Long effectiveUserId = convertToNullIfAllRecords(userId);

    return findToysWithFilters(effectiveUserId, status, campaignId, pageable);
  }

  @Override
  public long count(Long userId, Integer status, Long campaignId) {
    // Convert special value to null for filtering
    Long effectiveUserId = convertToNullIfAllRecords(userId);

    return countToysWithFilters(effectiveUserId, status, campaignId);
  }

  /**
   * Converts the special value ALL_RECORDS (-1) to null to indicate no filtering.
   * Any other value is returned as is.
   *
   * @param value The value to check
   * @return null if value is ALL_RECORDS, otherwise the original value
   */
  private Long convertToNullIfAllRecords(Long value) {
    return (value != null && value == ALL_RECORDS) ? null : value;
  }

  /**
   * Finds toys based on the provided filters and pagination.
   *
   * @param userId     User ID filter (null means no filtering)
   * @param status     Status filter
   * @param campaignId Campaign ID filter
   * @param pageable   Pagination information
   * @return List of filtered toys
   */
  private List<Toy> findToysWithFilters(Long userId, Integer status, Long campaignId, Pageable pageable) {
    // Case 1: All filters are present
    if (allFiltersPresent(userId, status, campaignId)) {
      return toyJPAMapper.findByUserIdAndStatus(userId, status, pageable).getContent();
    }

    // Case 2: User ID and Status filters
    if (userId != null && status != null) {
      return toyJPAMapper.findByUserIdAndStatus(userId, status, pageable).getContent();
    }

    // Case 3: User ID and Campaign ID filters
    if (userId != null && campaignId != null) {
      return toyJPAMapper.findByUserIdAndCampaignId(userId, campaignId, pageable).getContent();
    }

    // Case 4: Status and Campaign ID filters
    if (status != null && campaignId != null) {
      return toyJPAMapper.findAll(pageable).getContent();
    }

    // Case 5: Single filters
    if (userId != null) {
      return toyJPAMapper.findByUserId(userId, pageable).getContent();
    }
    if (status != null) {
      return toyJPAMapper.findByStatus(status, pageable).getContent();
    }
    if (campaignId != null) {
      return toyJPAMapper.findByCampaignId(campaignId, pageable).getContent();
    }

    // Case 6: No filters
    return toyJPAMapper.findAll(pageable).getContent();
  }

  /**
   * Counts toys based on the provided filters.
   *
   * @param userId     User ID filter (null means no filtering)
   * @param status     Status filter
   * @param campaignId Campaign ID filter
   * @return Total count of filtered toys
   */
  private long countToysWithFilters(Long userId, Integer status, Long campaignId) {
    // Case 1: All filters are present
    if (allFiltersPresent(userId, status, campaignId)) {
      return toyJPAMapper.countByUserIdAndCampaignId(userId, campaignId);
    }

    // Case 2: User ID and Status filters
    if (userId != null && status != null) {
      return toyJPAMapper.countByUserIdAndStatus(userId, status);
    }

    // Case 3: User ID and Campaign ID filters
    if (userId != null && campaignId != null) {
      return toyJPAMapper.countByUserIdAndCampaignId(userId, campaignId);
    }

    // Case 4: Status and Campaign ID filters
    if (status != null && campaignId != null) {
      return toyJPAMapper.count();
    }

    // Case 5: Single filters
    if (userId != null) {
      return toyJPAMapper.countByUserId(userId);
    }
    if (status != null) {
      return toyJPAMapper.countByStatus(status);
    }
    if (campaignId != null) {
      return toyJPAMapper.countByCampaignId(campaignId);
    }

    // Case 6: No filters
    return toyJPAMapper.count();
  }

  /**
   * Checks if all filters are present.
   *
   * @param userId     User ID filter
   * @param status     Status filter
   * @param campaignId Campaign ID filter
   * @return true if all filters are present
   */
  private boolean allFiltersPresent(Long userId, Integer status, Long campaignId) {
    return userId != null && status != null && campaignId != null;
  }

  @Override
  public List<Toy> findByCampaignIdAndStatus(Long campaignId, Integer status) {
    return toyJPAMapper.findByCampaignIdAndStatus(campaignId, status);
  }

  @Override
  public long countByCampaignIdAndStatus(Long campaignId, Integer status) {
    return toyJPAMapper.countByCampaignIdAndStatus(campaignId, status);
  }

  @Override
  public int updateStatusByCampaignId(Long campaignId, Integer oldStatus, Integer newStatus) {
    return 0;
  }

  /**
   * Create a Sort object from sortBy and sortDirection.
   *
   * @param sortBy        Field to sort by
   * @param sortDirection Sort direction (asc, desc)
   * @return Sort object
   */
  private Sort createSort(String sortBy, String sortDirection) {
    Direction direction = sortDirection.equalsIgnoreCase("asc") ? Direction.ASC : Direction.DESC;
    return Sort.by(direction, sortBy);
  }

  @Override
  public List<Toy> findByIdIn(List<Long> ids) {
    return toyJPAMapper.findByIdIn(ids);
  }

  @Override
  public List<Toy> findByIdInAndStatus(List<Long> ids, int status) {
    return toyJPAMapper.findByIdInAndStatus(ids, status);
  }

  @Override
  public int addToysToCampaign(List<Long> toyIds, Long campaignId) {
    return toyJPAMapper.updateStatusAndCampaignIdByIds(toyIds, campaignId, ToyStatus.GIVEAWAY_AVAILABLE.getValue());
  }

  @Override
  public List<Toy> findAll(Specification<Toy> specification, Pageable pageable) {
    // sort by id desc
    Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
        Sort.by(Sort.Direction.DESC, "id"));
    return toyJPAMapper.findAll(specification, sortedPageable).getContent();
  }

  @Override
  public List<Toy> findAll(Specification<Toy> specification) {
    return toyJPAMapper.findAll(specification);
  }

  @Override
  public Toy findRandomAvailableToyInCampaign(Long campaignId) {
    return toyJPAMapper.findRandomAvailableToyInCampaign(
        campaignId,
        ToyStatus.GIVEAWAY_AVAILABLE.getValue());
  }

  @Override
  public void updateStatus(Long toyId, Integer status) {
    toyJPAMapper.updateStatus(toyId, status);
  }

}