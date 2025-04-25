package locser.application.services.event;// toy-application/src/main/java/locser/toy/application/service/EventApplicationService.java

import java.util.List;

import locser.toy.domain.model.dto.CreateEventRequest;
import locser.toy.domain.model.dto.EventDTO;
import locser.toy.domain.model.dto.PageResponse;
import locser.toy.domain.model.dto.UpdateEventRequest;
import locser.toy.domain.model.entity.Event;

/**
 * Lớp dịch vụ ứng dụng cho Event, điều phối các use case.
 */
public interface EventApplicationService {

  /**
   * Chuyển đổi từ Entity sang DTO.
   */
  EventDTO mapToDTO(Event event);

  EventDTO createEvent(CreateEventRequest request);

  List<EventDTO> getAllEvents(int status);

  EventDTO getEventById(Long id);

  EventDTO updateEvent(Long id, UpdateEventRequest request);

  void deleteEvent(Long id);

  /**
   * Lấy danh sách sự kiện có phân trang.
   *
   * @param page   Số trang (bắt đầu từ 0)
   * @param limit  Kích thước trang
   * @param status Trạng thái sự kiện (tùy chọn)
   * @return Đối tượng PageResponse chứa danh sách sự kiện và thông tin phân trang
   */
  PageResponse<EventDTO> getEventsWithPagination(int page, int limit, int status);
}