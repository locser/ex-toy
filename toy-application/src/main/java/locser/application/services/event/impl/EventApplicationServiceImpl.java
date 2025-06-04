package locser.application.services.event.impl;

import java.util.List;
import java.util.stream.Collectors;
import locser.application.services.event.EventApplicationService;
import locser.toy.domain.model.dto.CreateEventRequest;
import locser.toy.domain.model.dto.EventDTO;
import locser.toy.domain.model.dto.UpdateEventRequest;
import locser.toy.domain.model.entity.Event;
import locser.toy.domain.repository.EventRepository;
import locser.toy.domain.service.EventDomainService;
import locser.util.PageResponse;
import org.springframework.stereotype.Service;

/**
 * Application service implementation for Event domain, orchestrating use cases.
 */
@Service
public class EventApplicationServiceImpl implements EventApplicationService {

  private final EventRepository eventRepository;
  private final EventDomainService eventDomainService;

  public EventApplicationServiceImpl(
      EventDomainService eventDomainService, EventRepository eventRepository) {
    this.eventRepository = eventRepository;
    this.eventDomainService = eventDomainService;
  }

  /**
   * Chuyển đổi từ Entity sang DTO.
   */
  @Override
  public EventDTO mapToDTO(Event event) {
    if (event == null) {
      return null;
    }

    EventDTO dto = new EventDTO();
    dto.setId(event.getId());
    dto.setName(event.getName());
    dto.setDescription(event.getDescription());
    dto.setStatus(event.getStatus());
    dto.setStartDate(event.getStartDate());
    dto.setEndDate(event.getEndDate());
    dto.setCreatedAt(event.getCreatedAt());
    dto.setUpdatedAt(event.getUpdatedAt());
    return dto;
  }

  @Override
  public EventDTO createEvent(CreateEventRequest request) {
    Event event = new Event();
    event.setName(request.getName());
    event.setDescription(request.getDescription());
    event.setStartDate(request.getStartDate());
    event.setEndDate(request.getEndDate());
    event.setTheme(request.getTheme());
    event.setRules(request.getRules());
    event = eventDomainService.initializeNewEvent(event);

    event = eventRepository.save(event);
    return mapToDTO(event);
  }

  @Override
  public List<EventDTO> getAllEvents(int status) {
    List<Event> events = eventRepository.findByStatus(status);
    return events.stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
  }

  @Override
  public List<EventDTO> getAllEvents(int status, String sortBy, String sortDirection) {
    List<Event> events = eventRepository.findByStatus(status, sortBy, sortDirection);
    return events.stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
  }

  @Override
  public EventDTO getEventById(Long id) {
    Event event = eventDomainService.getEventById(id);
    return mapToDTO(event);
  }

  @Override
  public EventDTO updateEvent(Long id, UpdateEventRequest request) {
    Event event = eventDomainService.getEventById(id);

    event.setName(request.getName());
    event.setDescription(request.getDescription());
    event.setStatus(request.getStatus());
    event.setStartDate(request.getStartDate());
    event.setEndDate(request.getEndDate());

    event = eventRepository.save(event);
    return mapToDTO(event);
  }

  @Override
  public void deleteEvent(Long id) {
    eventDomainService.deleteEvent(id);
  }

  @Override
  public void updateEventStatus(Long id, Integer status) {
    eventDomainService.updateEventStatus(id, status);
  }

  @Override
  public PageResponse<EventDTO> getEventsWithPagination(int page, int limit, int status) {
    List<Event> events = eventRepository.findWithPagination(page, limit, status, "id", "asc");
    long total = eventRepository.count(status);

    List<EventDTO> content = events.stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());

    return new PageResponse<>(content, limit, total);
  }

  @Override
  public PageResponse<EventDTO> getEventsWithPagination(int page, int limit, int status,
      String sortBy, String sortDirection) {
    List<Event> events = eventRepository.findWithPagination(page, limit, status, sortBy,
        sortDirection);
    long total = eventRepository.count(status);

    List<EventDTO> content = events.stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());

    return new PageResponse<>(content, limit, total);
  }
}