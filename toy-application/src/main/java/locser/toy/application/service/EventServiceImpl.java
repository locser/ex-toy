package locser.toy.application.service;

import locser.toy.domain.exception.ResourceNotFoundException;
import locser.toy.domain.model.dto.CreateEventRequest;
import locser.toy.domain.model.dto.EventDTO;
import locser.toy.domain.model.dto.UpdateEventRequest;
import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.enums.EventStatus;
import locser.toy.domain.repository.EventRepository;
import locser.toy.domain.service.EventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Triển khai các dịch vụ quản lý Sự kiện/Chiến dịch.
 */
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Chuyển đổi từ Entity sang DTO.
     */
    private EventDTO mapToDTO(Event event) {
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

    @Override
    @Transactional
    public EventDTO createEvent(CreateEventRequest request) {
        Event event = new Event();
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setTheme(request.getTheme());
        event.setRules(request.getRules());
        event.setStatus(EventStatus.UPCOMING);
        
        // Các giá trị mặc định sẽ được thiết lập bởi @PrePersist
        
        Event savedEvent = eventRepository.save(event);
        return mapToDTO(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDTO> getAllEvents(Optional<EventStatus> status) {
        List<Event> events;
        
        if (status.isPresent()) {
            events = eventRepository.findByStatus(status.get());
        } else {
            events = eventRepository.findAll();
        }
        
        return events.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sự kiện với ID: " + id));
        
        return mapToDTO(event);
    }

    @Override
    @Transactional
    public EventDTO updateEvent(Long id, UpdateEventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sự kiện với ID: " + id));
        
        // Chỉ cập nhật các trường không null
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
        
        // updatedAt sẽ được cập nhật tự động bởi @PreUpdate
        
        Event updatedEvent = eventRepository.save(event);
        return mapToDTO(updatedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sự kiện với ID: " + id));
        
        // Soft delete: Chỉ đánh dấu là đã xóa
        event.setStatus(EventStatus.DELETED);
        eventRepository.save(event);
        
        // Hoặc hard delete nếu cần
        // eventRepository.delete(event);
    }
}
