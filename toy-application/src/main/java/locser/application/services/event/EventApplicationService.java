package locser.application.services.event;

import java.util.List;
import locser.toy.domain.model.dto.CreateEventRequest;
import locser.toy.domain.model.dto.EventDTO;
import locser.toy.domain.model.dto.UpdateEventRequest;
import locser.toy.domain.model.entity.Event;
import locser.util.PageResponse;

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

  List<EventDTO> getAllEvents(int status, String sortBy, String sortDirection);

  EventDTO getEventById(Long id);

  EventDTO updateEvent(Long id, UpdateEventRequest request);

  void deleteEvent(Long id);

  void updateEventStatus(Long id, Integer status);

  /**
   * Lấy danh sách sự kiện có phân trang.
   *
   * @param page   Số trang (bắt đầu từ 0)
   * @param limit  Kích thước trang
   * @param status Trạng thái sự kiện (tùy chọn)
   * @return Đối tượng PageResponse chứa danh sách sự kiện và thông tin phân trang
   */
  PageResponse<EventDTO> getEventsWithPagination(int page, int limit, int status);

  /**
   * Lấy danh sách sự kiện có phân trang và sắp xếp.
   *
   * @param page          Số trang (bắt đầu từ 0)
   * @param limit         Kích thước trang
   * @param status        Trạng thái sự kiện (tùy chọn)
   * @param sortBy        Trường để sắp xếp (id, start_date)
   * @param sortDirection Hướng sắp xếp (asc, desc)
   * @return Đối tượng PageResponse chứa danh sách sự kiện và thông tin phân trang
   */
  PageResponse<EventDTO> getEventsWithPagination(int page, int limit, int status, String sortBy,
      String sortDirection);
}