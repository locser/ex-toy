package locser.controller.http;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import locser.application.services.event.EventApplicationService;
import locser.controller.dto.CreateEventRequestDTO;
import locser.controller.dto.EventResponseDTO;
import locser.controller.dto.PageResponseDTO;
import locser.controller.dto.UpdateEventRequestDTO;
import locser.controller.mapper.EventDTOMapper;
import locser.controller.response.BaseResponse;
import locser.toy.domain.model.dto.EventDTO;
import locser.toy.domain.model.enums.EventStatus;

/**
 * Controller xử lý các API liên quan đến Sự kiện/Chiến dịch.
 */
@RestController
@RequestMapping("/api/v1")
public class EventController {

        private final EventApplicationService eventService;

        public EventController(EventApplicationService eventService) {
                this.eventService = eventService;
        }

        /**
         * Tạo mới một sự kiện.
         *
         * @param request Thông tin sự kiện cần tạo
         * @return Sự kiện đã được tạo
         */
        @PostMapping("/admin/campaigns")
        public BaseResponse createEvent(@Valid @RequestBody CreateEventRequestDTO requestDTO) {
                eventService.createEvent(EventDTOMapper.toCreateEventRequest(requestDTO));
                return BaseResponse.success();
        }

        /**
         * Lấy danh sách tất cả các sự kiện, có thể lọc theo trạng thái.
         *
         * @param status Trạng thái sự kiện để lọc (tùy chọn)
         * @return Danh sách các sự kiện
         */
        @GetMapping("/campaigns")
        public BaseResponse<List<EventResponseDTO>> getAllEvents(@RequestParam(required = true) EventStatus status) {
                List<EventDTO> events = eventService.getAllEvents(status);
                List<EventResponseDTO> responseDTOs = events.stream()
                                .map(EventDTOMapper::toEventResponseDTO)
                                .collect(Collectors.toList());
                return BaseResponse.success(responseDTOs);
        }

        /**
         * Lấy danh sách sự kiện có phân trang, có thể lọc theo trạng thái.
         *
         * @param page   Số trang (bắt đầu từ 0)
         * @param limit  Kích thước trang
         * @param status Trạng thái sự kiện để lọc (tùy chọn)
         * @return Đối tượng phân trang chứa danh sách sự kiện
         */
        @GetMapping("/campaigns/pagination")
        public BaseResponse<PageResponseDTO<EventResponseDTO>> getEventsWithPagination(
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "20") int limit,
                        @RequestParam(required = false) EventStatus status) {
                var pageResponse = eventService.getEventsWithPagination(page, limit, status);
                var pageResponseDTO = EventDTOMapper.toPageResponseDTO(pageResponse);
                return BaseResponse.success(pageResponseDTO);
        }

        /**
         * Lấy thông tin chi tiết của một sự kiện theo ID.
         *
         * @param id ID của sự kiện
         * @return Thông tin chi tiết sự kiện
         */
        @GetMapping("/campaigns/{id}")
        public BaseResponse<EventResponseDTO> getEventById(@PathVariable Long id) {
                EventDTO event = eventService.getEventById(id);
                EventResponseDTO responseDTO = EventDTOMapper.toEventResponseDTO(event);
                return BaseResponse.success(responseDTO);
        }

        /**
         * Cập nhật thông tin của một sự kiện.
         *
         * @param id      ID của sự kiện cần cập nhật
         * @param request Thông tin cập nhật
         * @return Sự kiện đã được cập nhật
         */
        @PostMapping("/admin/campaigns/{id}")
        public ResponseEntity<Void> updateEvent(@PathVariable Long id, @RequestBody UpdateEventRequestDTO requestDTO) {
                eventService.updateEvent(id, EventDTOMapper.toUpdateEventRequest(requestDTO));
                return ResponseEntity.ok().build();
        }

        /**
         * Xóa một sự kiện.
         *
         * @param id ID của sự kiện cần xóa
         * @return Thông báo kết quả
         */
        @PostMapping("/admin/campaigns/{id}/delete")
        public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
                eventService.deleteEvent(id);
                return ResponseEntity.ok().build();
        }
}
