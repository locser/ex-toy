package locser.toy.domain.repository;

import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.enums.EventStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface để truy cập dữ liệu Event.
 */
public interface EventRepository {

    /**
     * Tìm một sự kiện theo ID.
     *
     * @param id ID của sự kiện
     * @return Optional chứa sự kiện nếu tìm thấy, hoặc empty nếu không tìm thấy
     */
    Optional<Event> findById(Long id);

    /**
     * Tìm một sự kiện theo ID (phương thức cũ).
     *
     * @param id ID của sự kiện
     * @return Optional chứa sự kiện nếu tìm thấy, hoặc empty nếu không tìm thấy
     * @deprecated Sử dụng {@link #findById(Long)} thay thế
     */
    @Deprecated
    Optional<Event> findOneById(Long id);

    /**
     * Tìm tất cả các sự kiện.
     *
     * @return Danh sách tất cả các sự kiện
     */
    List<Event> findAll();

    /**
     * Tìm các sự kiện theo trạng thái.
     *
     * @param status Trạng thái cần tìm
     * @return Danh sách các sự kiện có trạng thái tương ứng
     */
    List<Event> findByStatus(EventStatus status);

    /**
     * Lưu một sự kiện.
     *
     * @param event Sự kiện cần lưu
     * @return Sự kiện đã được lưu
     */
    Event save(Event event);

    /**
     * Xóa một sự kiện.
     *
     * @param event Sự kiện cần xóa
     */
    void delete(Event event);
}