package locser.controller.http;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import locser.application.services.event.EventApplicationService;
import locser.controller.dto.event.CreateEventRequestDTO;
import locser.controller.dto.event.EventResponseDTO;
import locser.controller.dto.event.UpdateEventRequestDTO;
import locser.controller.dto.event.UpdateEventStatusRequestDTO;
import locser.controller.mapper.EventDTOMapper;
import locser.controller.response.BaseResponse;
import locser.toy.domain.model.dto.EventDTO;
import locser.toy.domain.model.enums.EventStatus;
import locser.toy.domain.validation.annotation.ValidId;
import locser.util.PageResponseDTO;
import locser.utils.AppConstants;
import locser.utils.AppUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
   * Lấy danh sách tất cả các sự kiện, có thể lọc theo trạng thái và sắp xếp.
   *
   * @param status        Trạng thái sự kiện để lọc (tùy chọn)
   * @param sortBy        Trường để sắp xếp (id, start_date)
   * @param sortDirection Hướng sắp xếp (asc, desc)
   * @return Danh sách các sự kiện
   */
  @GetMapping("/campaigns")
  public BaseResponse<List<EventResponseDTO>> getAllEvents(
      @RequestParam(required = true) EventStatus status,
      @RequestParam(name = "sort_by", required = false, defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
      @RequestParam(name = "sort_direction", required = false, defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDirection) {

    List<EventDTO> events = eventService.getAllEvents(status.getValue(), sortBy, sortDirection);
    List<EventResponseDTO> responseDTOs = events.stream()
        .map(EventDTOMapper::toEventResponseDTO)
        .collect(Collectors.toList());
    return BaseResponse.success(responseDTOs);
  }

  /**
   * Lấy danh sách sự kiện có phân trang, có thể lọc theo trạng thái và sắp xếp.
   *
   * @param page          Số trang (bắt đầu từ 0)
   * @param limit         Kích thước trang
   * @param status        Trạng thái sự kiện để lọc (tùy chọn)
   * @param sortBy        Trường để sắp xếp (id, start_date)
   * @param sortDirection Hướng sắp xếp (asc, desc)
   * @return Đối tượng phân trang chứa danh sách sự kiện
   */
  @GetMapping("/campaigns/pagination")
  public BaseResponse<PageResponseDTO<EventResponseDTO>> getEventsWithPagination(
      @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
      @RequestParam(name = "limit", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer limit,
      @RequestParam(defaultValue = "-1", name = "status") Integer status,
      @RequestParam(name = "sort_by", required = false, defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
      @RequestParam(name = "sort_direction", required = false, defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDirection) {
    AppUtils.validatePageNumberAndSize(page, limit);

    var pageResponse = eventService.getEventsWithPagination(page, limit, status, sortBy,
        sortDirection);
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
  public BaseResponse<EventResponseDTO> getEventById(
      @ValidId(entity = "Event") @PathVariable Long id) {
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
  public BaseResponse updateEvent(@ValidId(entity = "Event") @PathVariable Long id,
      @RequestBody UpdateEventRequestDTO requestDTO) {
    eventService.updateEvent(id, EventDTOMapper.toUpdateEventRequest(requestDTO));
    return BaseResponse.success();
  }

  /**
   * Xóa một sự kiện.
   *
   * @param id ID của sự kiện cần xóa
   * @return Thông báo kết quả
   */
  @PostMapping("/admin/campaigns/{id}/delete")
  public BaseResponse deleteEvent(@ValidId(entity = "Event") @PathVariable Long id) {
    eventService.deleteEvent(id);
    return BaseResponse.success();
  }

  /**
   * Cập nhật trạng thái của một sự kiện.
   *
   * @param id         ID của sự kiện cần cập nhật
   * @param requestDTO Thông tin trạng thái mới
   * @return Thông báo kết quả
   */
  @PostMapping("/admin/campaigns/{id}/status")
  public BaseResponse updateEventStatus(@ValidId(entity = "Event") @PathVariable Long id,
      @Valid @RequestBody UpdateEventStatusRequestDTO requestDTO) {
    eventService.updateEventStatus(id, requestDTO.getStatus());
    return BaseResponse.success();
  }
}