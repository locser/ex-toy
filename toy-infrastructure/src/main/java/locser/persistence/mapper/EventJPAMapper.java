package locser.persistence.mapper;

import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository interface cho entity Event.
 */
public interface EventJPAMapper extends JpaRepository<Event, Long> {

    /**
     * Tìm một sự kiện theo ID (phương thức cũ).
     *
     * @param id ID của sự kiện
     * @return Optional chứa sự kiện nếu tìm thấy, hoặc empty nếu không tìm thấy
     * @deprecated Sử dụng {@link #findById(Object)} thay thế
     */
    @Deprecated
    Optional<Event> findOneById(Long id);

    /**
     * Tìm các sự kiện theo trạng thái.
     *
     * @param status Trạng thái cần tìm
     * @return Danh sách các sự kiện có trạng thái tương ứng
     */
    List<Event> findByStatus(EventStatus status);
}
