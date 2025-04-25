package locser.application.services.event.impl;// toy-application/src/main/java/locser/toy/application/service/EventApplicationService.java

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import locser.application.services.event.EventApplicationService;
import locser.toy.domain.model.dto.CreateEventRequest;
import locser.toy.domain.model.dto.EventDTO;
import locser.toy.domain.model.dto.PageResponse;
import locser.toy.domain.model.dto.UpdateEventRequest;
import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.enums.EventStatus;
import locser.toy.domain.repository.EventRepository;
import locser.toy.domain.service.EventDomainService;

/**
 * Lớp dịch vụ ứng dụng cho Event, điều phối các use case.
 */
@Service
public class EventApplicationServiceImpl implements EventApplicationService {

  private final EventRepository eventRepository;
  private final EventDomainService eventDomainService;

  public EventApplicationServiceImpl(EventRepository eventRepository,
      EventDomainService eventDomainService) {
    this.eventRepository = eventRepository;
    this.eventDomainService = eventDomainService;
  }

  /**
   * Chuyển đổi từ Entity sang DTO.
   */
  public EventDTO mapToDTO(Event event) {
    return EventDTO.builder()
        .id(event.getId())
        .name(event.getName())
        .description(event.getDescription())
        .startDate(event.getStartDate())
        .endDate(event.getEndDate())
        .theme(event.getTheme())
        .rules(event.getRules())
        .status(event.getStatus())
        .createdAt(event.getCreatedAt())
        .updatedAt(event.getUpdatedAt())
        .build();
  }

  public EventDTO createEvent(CreateEventRequest request) {
    // Tạo entity từ request
    Event event = new Event();
    event.setName(request.getName());
    event.setDescription(request.getDescription());
    event.setStartDate(request.getStartDate());
    event.setEndDate(request.getEndDate());
    event.setTheme(request.getTheme());
    event.setRules(request.getRules());

    // Gọi domain service để xử lý logic nghiệp vụ
    event = eventDomainService.initializeNewEvent(event);

    // Lưu vào repository
    Event savedEvent = eventRepository.save(event);

    // Chuyển đổi và trả về DTO
    return mapToDTO(savedEvent);
  }

  @Override
  public List<EventDTO> getAllEvents(int status) {
    List<Event> events;

    if (status != EventStatus.ALL.getValue()) {
      events = eventRepository.findByStatus(status);
    } else {
      events = eventRepository.findAll();
    }

    return events.stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
  }

  public EventDTO getEventById(Long id) {
    Event event = eventDomainService.getEventById(id);
    return mapToDTO(event);
  }

  public EventDTO updateEvent(Long id, UpdateEventRequest request) {
    // Lấy event hiện tại
    Event event = eventDomainService.getEventById(id);

    // Cập nhật thông tin
    if (request.getName() != null) {
      event.setName(request.getName());
    }

    if (request.getDescription() != null) {
      event.setDescription(request.getDescription());
    }

    if (request.getStartDate() != null) {
      event.setStartDate(request.getStartDate());
    }

    if (request.getEndDate() != null) {
      event.setEndDate(request.getEndDate());
    }

    if (request.getTheme() != null) {
      event.setTheme(request.getTheme());
    }

    if (request.getRules() != null) {
      event.setRules(request.getRules());
    }

    if (request.getStatus() != null) {
      event.setStatus(request.getStatus());
    }

    // Gọi domain service để xác thực và xử lý logic nghiệp vụ
    event = eventDomainService.validateAndUpdateEvent(event);

    // Lưu vào repository
    Event updatedEvent = eventRepository.save(event);

    // Chuyển đổi và trả về DTO
    return mapToDTO(updatedEvent);
  }

  public void deleteEvent(Long id) {
    eventDomainService.deleteEvent(id);
  }

  @Override
  public PageResponse<EventDTO> getEventsWithPagination(int page, int limit, int status) {

    // Lấy danh sách sự kiện theo trang
    List<Event> events = eventRepository.findWithPagination(page, limit, status);

    // Đếm tổng số sự kiện
    long totalRecords = eventRepository.count(status);

    // Chuyển đổi sang DTO
    List<EventDTO> eventDTOs = events.stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());

    // Tạo đối tượng phân trang
    return PageResponse.of(eventDTOs, limit, totalRecords);
  }
}
