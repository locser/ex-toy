package locser.toy.domain.service;

import locser.toy.domain.model.entity.Event;


/**
 * Interface định nghĩa các dịch vụ miền cho Event.
 */
public interface EventDomainService {

  /**
   * Khởi tạo một sự kiện mới với các giá trị mặc định.
   *
   * @param event Sự kiện cần khởi tạo
   * @return Sự kiện đã được khởi tạo
   */
  Event initializeNewEvent(Event event);

  /**
   * Lấy sự kiện theo ID.
   *
   * @param id ID của sự kiện
   * @return Sự kiện
   * @throws ResourceNotFoundException nếu không tìm thấy
   */
  Event getEventById(Long id);

  /**
   * Xác thực và cập nhật sự kiện.
   *
   * @param event Sự kiện cần cập nhật
   * @return Sự kiện đã được cập nhật
   */
  Event validateAndUpdateEvent(Event event);

  /**
   * Xóa sự kiện.
   *
   * @param id ID của sự kiện cần xóa
   */
  void deleteEvent(Long id);
}