package locser.toy.domain.repository;

import java.util.List;
import java.util.Optional;

import locser.toy.domain.model.entity.Event;

public interface EventRepository {

  Optional<Event> findOneById(Long id);

  Event save(Event event);

  Event findById(Long id);

  List<Event> findByStatus(int status);

  List<Event> findAll();

  /**
   * Lấy danh sách sự kiện có phân trang.
   *
   * @param page   Số trang (bắt đầu từ 0)
   * @param size   Kích thước trang
   * @param status Trạng thái sự kiện (tùy chọn)
   * @return Danh sách sự kiện theo trang
   */
  List<Event> findWithPagination(int page, int size, int status);

  /**
   * Đếm tổng số sự kiện.
   *
   * @param status Trạng thái sự kiện (tùy chọn)
   * @return Tổng số sự kiện
   */
  long count(int status);
}