package locser;

import jakarta.validation.Valid;
import locser.toy.controller.response.ApiResponse;
import locser.toy.domain.model.dto.CreateEventRequest;
import locser.toy.domain.model.dto.EventDTO;
import locser.toy.domain.model.dto.UpdateEventRequest;
import locser.toy.domain.model.enums.EventStatus;
import locser.toy.domain.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller xử lý các API liên quan đến Sự kiện/Chiến dịch.
 */
@RestController
@RequestMapping("/api/v1")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Tạo mới một sự kiện (chỉ admin).
     *
     * @param request Thông tin sự kiện cần tạo
     * @return Sự kiện đã được tạo
     */
    @PostMapping("/admin/campaigns")
    public ResponseEntity<ApiResponse<EventDTO>> createEvent(@Valid @RequestBody CreateEventRequest request) {
        EventDTO createdEvent = eventService.createEvent(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdEvent, "Tạo sự kiện thành công"));
    }

    /**
     * Lấy danh sách tất cả các sự kiện, có thể lọc theo trạng thái.
     *
     * @param status Trạng thái sự kiện để lọc (tùy chọn)
     * @return Danh sách các sự kiện
     */
    @GetMapping("/campaigns")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getAllEvents(
            @RequestParam(required = false) EventStatus status) {
        List<EventDTO> events = eventService.getAllEvents(Optional.ofNullable(status));
        return ResponseEntity.ok(ApiResponse.success(events, "Lấy danh sách sự kiện thành công"));
    }

    /**
     * Lấy thông tin chi tiết của một sự kiện theo ID.
     *
     * @param id ID của sự kiện
     * @return Thông tin chi tiết sự kiện
     */
    @GetMapping("/campaigns/{id}")
    public ResponseEntity<ApiResponse<EventDTO>> getEventById(@PathVariable Long id) {
        EventDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(ApiResponse.success(event, "Lấy thông tin sự kiện thành công"));
    }

    /**
     * Cập nhật thông tin của một sự kiện (chỉ admin).
     *
     * @param id ID của sự kiện cần cập nhật
     * @param request Thông tin cập nhật
     * @return Sự kiện đã được cập nhật
     */
    @PutMapping("/admin/campaigns/{id}")
    public ResponseEntity<ApiResponse<EventDTO>> updateEvent(
            @PathVariable Long id,
            @RequestBody UpdateEventRequest request) {
        EventDTO updatedEvent = eventService.updateEvent(id, request);
        return ResponseEntity.ok(ApiResponse.success(updatedEvent, "Cập nhật sự kiện thành công"));
    }

    /**
     * Xóa một sự kiện (chỉ admin).
     *
     * @param id ID của sự kiện cần xóa
     * @return Thông báo kết quả
     */
    @DeleteMapping("/admin/campaigns/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa sự kiện thành công"));
    }
}
