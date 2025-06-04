package locser.persistence.repository;

import java.util.List;
import java.util.Optional;
import locser.persistence.mapper.ToyJPAMapper;
import locser.toy.domain.model.entity.Toy;
import locser.toy.domain.repository.ToyRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

/**
 * Implementation of ToyRepository using JPA.
 */
@Service
public class ToyInfrasRepositoryImpl implements ToyRepository {

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

    if (userId != null && status != null && campaignId != null) {
      // Tìm theo userId, status và campaignId
      return toyJPAMapper.findByUserIdAndStatus(userId, status, pageable).getContent();
    } else if (userId != null && status != null) {
      // Tìm theo userId và status
      return toyJPAMapper.findByUserIdAndStatus(userId, status, pageable).getContent();
    } else if (userId != null && campaignId != null) {
      // Tìm theo userId và campaignId
      return toyJPAMapper.findByUserIdAndCampaignId(userId, campaignId, pageable).getContent();
    } else if (status != null && campaignId != null) {
      // Tìm theo status và campaignId (cần thêm method trong JPA repository)
      return toyJPAMapper.findAll(pageable).getContent();
    } else if (userId != null) {
      // Tìm theo userId
      return toyJPAMapper.findByUserId(userId, pageable).getContent();
    } else if (status != null) {
      // Tìm theo status
      return toyJPAMapper.findByStatus(status, pageable).getContent();
    } else if (campaignId != null) {
      // Tìm theo campaignId
      return toyJPAMapper.findByCampaignId(campaignId, pageable).getContent();
    } else {
      // Tìm tất cả
      return toyJPAMapper.findAll(pageable).getContent();
    }
  }

  @Override
  public long count(Long userId, Integer status, Long campaignId) {
    if (userId != null && status != null && campaignId != null) {
      // Đếm theo userId, status và campaignId
      return toyJPAMapper.countByUserIdAndCampaignId(userId, campaignId);
    } else if (userId != null && status != null) {
      // Đếm theo userId và status
      return toyJPAMapper.countByUserIdAndStatus(userId, status);
    } else if (userId != null && campaignId != null) {
      // Đếm theo userId và campaignId
      return toyJPAMapper.countByUserIdAndCampaignId(userId, campaignId);
    } else if (status != null && campaignId != null) {
      // Đếm theo status và campaignId (cần thêm method trong JPA repository)
      return toyJPAMapper.count();
    } else if (userId != null) {
      // Đếm theo userId
      return toyJPAMapper.countByUserId(userId);
    } else if (status != null) {
      // Đếm theo status
      return toyJPAMapper.countByStatus(status);
    } else if (campaignId != null) {
      // Đếm theo campaignId
      return toyJPAMapper.countByCampaignId(campaignId);
    } else {
      // Đếm tất cả
      return toyJPAMapper.count();
    }
  }

  @Override
  public List<Toy> findByCampaignIdAndStatus(Long campaignId, Integer status) {
    return List.of();
  }

  @Override
  public long countByCampaignIdAndStatus(Long campaignId, Integer status) {
    return 0;
  }

  @Override
  public int updateStatusByCampaignId(Long campaignId, Integer oldStatus, Integer newStatus) {
    return 0;
  }

  @Override
  public int addToysToCampaign(List<Long> toyIds, Long campaignId, Integer newStatus) {
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
}