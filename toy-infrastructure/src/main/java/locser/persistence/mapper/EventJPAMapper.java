package locser.persistence.mapper;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.enums.EventStatus;

public interface EventJPAMapper extends JpaRepository<Event, Long> {

    Optional<Event> findOneById(Long id);

    List<Event> findByStatus(EventStatus status);

    /**
     * Lấy danh sách sự kiện theo trạng thái với phân trang.
     *
     * @param status   Trạng thái sự kiện
     * @param pageable Thông tin phân trang
     * @return Trang sự kiện
     */
    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    /**
     * Đếm số lượng sự kiện theo trạng thái.
     *
     * @param status Trạng thái sự kiện
     * @return Số lượng sự kiện
     */
    long countByStatus(EventStatus status);
}
