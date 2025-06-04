package locser.toy.domain.repository;

import java.util.List;
import java.util.Optional;
import locser.toy.domain.model.entity.Event;

public interface EventRepository {

  Optional<Event> findOneById(Long id);

  Event save(Event event);

  Event findById(Long id);

  List<Event> findByStatus(int status);

  List<Event> findByStatus(int status, String sortBy, String sortDirection);

  List<Event> findAll();

  List<Event> findAll(String sortBy, String sortDirection);

  /**
   * Lấy danh sách sự kiện có phân trang.
   *
   * @param page          Số trang (bắt đầu từ 0)
   * @param size          Kích thước trang
   * @param status        Trạng thái sự kiện (tùy chọn)
   * @param sortBy        Trường để sắp xếp (id, startDate)
   * @param sortDirection Hướng sắp xếp (asc, desc)
   * @return Danh sách sự kiện theo trang
   */
  List<Event> findWithPagination(int page, int size, int status, String sortBy,
      String sortDirection);

  /**
   * Đếm tổng số sự kiện.
   *
   * @param status Trạng thái sự kiện (tùy chọn)
   * @return Tổng số sự kiện
   */
  long count(int status);

  /**
   * Find events by type.
   *
   * @param type Event type (EXCHANGE=1, GIVEAWAY=2)
   * @return List of events with the given type
   */
  List<Event> findByType(Integer type);

  /**
   * Find events by type with pagination.
   *
   * @param type          Event type (EXCHANGE=1, GIVEAWAY=2)
   * @param page          Page number (starting from 0)
   * @param size          Page size
   * @param status        Event status (optional)
   * @param sortBy        Field to sort by
   * @param sortDirection Sort direction (asc, desc)
   * @return List of events with the given type
   */
  List<Event> findByTypeWithPagination(Integer type, int page, int size, Integer status,
      String sortBy,
      String sortDirection);

  /**
   * Find an event by ID and type.
   *
   * @param id   Event ID
   * @param type Event type
   * @return An Optional containing the event if found
   */
  Optional<Event> findByIdAndType(Long id, Integer type);

  /**
   * Count events by type.
   *
   * @param type   Event type
   * @param status Event status (optional)
   * @return Number of events with the given type
   */
  long countByType(Integer type, Integer status);
}