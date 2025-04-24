package locser.toy.domain.service;

import locser.toy.domain.model.dto.CreateEventRequest;
import locser.toy.domain.model.dto.EventDTO;
import locser.toy.domain.model.dto.UpdateEventRequest;
import locser.toy.domain.model.enums.EventStatus;

import java.util.List;
import java.util.Optional;

/**
 * Interface định nghĩa các dịch vụ liên quan đến quản lý Sự kiện/Chiến dịch.
 */
public interface EventService {
    
    /**
     * Tạo mới một sự kiện.
     *
     * @param request Thông tin sự kiện cần tạo
     * @return Sự kiện đã được tạo
     */
    EventDTO createEvent(CreateEventRequest request);
    
    /**
     * Lấy danh sách tất cả các sự kiện, có thể lọc theo trạng thái.
     *
     * @param status Trạng thái sự kiện để lọc (tùy chọn)
     * @return Danh sách các sự kiện
     */
    List<EventDTO> getAllEvents(Optional<EventStatus> status);
    
    /**
     * Lấy thông tin chi tiết của một sự kiện theo ID.
     *
     * @param id ID của sự kiện
     * @return Thông tin chi tiết sự kiện
     */
    EventDTO getEventById(Long id);
    
    /**
     * Cập nhật thông tin của một sự kiện.
     *
     * @param id ID của sự kiện cần cập nhật
     * @param request Thông tin cập nhật
     * @return Sự kiện đã được cập nhật
     */
    EventDTO updateEvent(Long id, UpdateEventRequest request);
    
    /**
     * Xóa một sự kiện.
     *
     * @param id ID của sự kiện cần xóa
     */
    void deleteEvent(Long id);
}
