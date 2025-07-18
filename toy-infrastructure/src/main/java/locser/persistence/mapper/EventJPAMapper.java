package locser.persistence.mapper;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import locser.toy.domain.model.entity.Event;

@Repository
public interface EventJPAMapper extends JpaRepository<Event, Long> {

    Optional<Event> findOneById(Long id);

    Optional<Event> findByIdAndType(Long id, Integer type);

    List<Event> findByStatus(int status);

    List<Event> findByStatus(int status, Sort sort);

    /**
     * Lấy danh sách sự kiện theo trạng thái với phân trang.
     *
     * @param status   Trạng thái sự kiện
     * @param pageable Thông tin phân trang
     * @return Trang sự kiện
     */
    Page<Event> findByStatus(int status, Pageable pageable);

    /**
     * Đếm số lượng sự kiện theo trạng thái.
     *
     * @param status Trạng thái sự kiện
     * @return Số lượng sự kiện
     */
    long countByStatus(int status);

    /**
     * Decrement the available toys count for a campaign.
     * This is an atomic operation that will only succeed if there are toys
     * available.
     *
     * @param campaignId The campaign ID
     * @return Number of rows updated (1 if successful, 0 if no toys available)
     */
    @Modifying
    @Query("UPDATE Event e SET e.availableToys = e.availableToys - :count WHERE e.id = :campaignId AND e.availableToys > 0")
    void decrementAvailableToys(@Param("campaignId") Long campaignId, @Param("count") int count);
}
