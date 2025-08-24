package locser.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import locser.persistence.mapper.EventJPAMapper;
import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.enums.EventStatus;
import locser.toy.domain.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EventInfrasRepositoryImpl implements EventRepository {

  private final EventJPAMapper eventJPAMapper;

  public EventInfrasRepositoryImpl(EventJPAMapper eventJPAMapper) {
    this.eventJPAMapper = eventJPAMapper;
  }

  @Override
  public Optional<Event> findOneById(Long id) {
    log.info("GET EVENT FROM DATABASE");
    return this.eventJPAMapper.findOneById(id);
  }

  @Override
  public Event save(Event event) {
    return eventJPAMapper.save(event);
  }

  @Override
  public Event findById(Long id) {
    log.info("GET EVENT FROM DATABASE");
    return eventJPAMapper.findById(id).orElse(null);
  }

  @Override
  public List<Event> findByStatus(int status) {
    System.out.println("GET EVENT FROM DATABASE");
    return eventJPAMapper.findByStatus(status);
  }

  @Override
  public List<Event> findByStatus(int status, String sortBy, String sortDirection) {
    System.out.println("GET EVENT FROM DATABASE");
    Sort sort = createSort(sortBy, sortDirection);
    return eventJPAMapper.findByStatus(status, sort);
  }

  @Override
  public List<Event> findAll() {
    System.out.println("GET EVENT FROM DATABASE");
    return eventJPAMapper.findAll();
  }

  @Override
  public List<Event> findAll(String sortBy, String sortDirection) {
    System.out.println("GET EVENT FROM DATABASE");
    Sort sort = createSort(sortBy, sortDirection);
    return eventJPAMapper.findAll(sort);
  }

  @Override
  public List<Event> findWithPagination(int page, int size, int status, String sortBy,
      String sortDirection) {
    System.out.println("GET EVENT FROM DATABASE");
    Sort sort = createSort(sortBy, sortDirection);
    Pageable pageable = PageRequest.of(page, size, sort);

    System.out.println(
        "page: " + page + ", size: " + size + ", status: " + status + ", sortBy: " + sortBy
            + ", sortDirection: " + sortDirection);

    if (status != EventStatus.ALL.getValue()) {
      return eventJPAMapper.findByStatus(status, pageable).getContent();
    } else {
      return eventJPAMapper.findAll(pageable).getContent();
    }
  }

  @Override
  public long count(int status) {
    System.out.println("GET EVENT FROM DATABASE");
    if (status != EventStatus.ALL.getValue()) {
      return eventJPAMapper.countByStatus(status);
    } else {
      return eventJPAMapper.count();
    }
  }

  @Override
  public List<Event> findByType(Integer type) {
    System.out.println("GET EVENT FROM DATABASE");
    return List.of();
  }

  @Override
  public List<Event> findByTypeWithPagination(Integer type, int page, int size, Integer status,
      String sortBy, String sortDirection) {
    System.out.println("GET EVENT FROM DATABASE");
    return List.of();
  }

  @Override
  public Optional<Event> findByIdAndType(Long id, Integer type) {
    System.out.println("id: " + id + " type: " + type);
    return eventJPAMapper.findByIdAndType(id, type);
  }

  @Override
  public long countByType(Integer type, Integer status) {
    System.out.println("GET EVENT FROM DATABASE");
    return 0;
  }

  @Override
  public void decrementAvailableToys(Long campaignId, int count) {
    eventJPAMapper.decrementAvailableToys(campaignId, count);
  }

  /**
   * Tạo đối tượng Sort dựa trên tên trường và hướng sắp xếp.
   *
   * @param sortBy        Tên trường để sắp xếp
   * @param sortDirection Hướng sắp xếp (asc, desc)
   * @return Đối tượng Sort
   */
  private Sort createSort(String sortBy, String sortDirection) {
    // Mặc định sắp xếp theo id tăng dần
    String field = "id";
    Direction direction = Direction.ASC;

    // Xác định trường sắp xếp
    if (sortBy != null && !sortBy.isEmpty()) {
      if (sortBy.equalsIgnoreCase("id") || sortBy.equalsIgnoreCase("start_date")) {
        field = sortBy.equalsIgnoreCase("start_date") ? "startDate" : sortBy;
      }
    }

    // Xác định hướng sắp xếp
    if (sortDirection != null) {
      if (sortDirection.equalsIgnoreCase("desc")) {
        direction = Direction.DESC;
      } else if (sortDirection.equalsIgnoreCase("asc")) {
        direction = Direction.ASC;
      }
    }

    return Sort.by(direction, field);
  }

  @Override
  public Event findByIdAndTypeAndStatus(Long id, Integer type, Integer status) {
    System.out.println("EventInfrasRepositoryImpl findByIdAndTypeAndStatus: " + id + " " + type + " " + status);
    return eventJPAMapper.findByIdAndTypeAndStatus(id, type, status);
  }
}