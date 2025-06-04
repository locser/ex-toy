package locser.toy.domain.service.impl;

import org.springframework.stereotype.Service;

import locser.toy.domain.exception.BadRequestException;
import locser.toy.domain.exception.ResourceNotFoundException;
import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.enums.EventStatus;
import locser.toy.domain.repository.EventRepository;
import locser.toy.domain.service.EventDomainService;
import locser.toy.domain.validation.IdValidator;

/**
 * Triển khai các dịch vụ miền cho Event.
 */
@Service
public class EventDomainServiceImpl implements EventDomainService {

  private final EventRepository eventRepository;

  public EventDomainServiceImpl(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  @Override
  public Event initializeNewEvent(Event event) {
    // Thiết lập các giá trị mặc định
    event.setStatus(EventStatus.UPCOMING.getValue());

    // Xác thực dữ liệu
    validateEventDates(event);

    return event;
  }

  @Override
  public Event getEventById(Long id) {
    // Kiểm tra ID phải lớn hơn 0
    IdValidator.validateId(id, "Event");

    return eventRepository.findOneById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sự kiện với ID: " + id));
  }

  @Override
  public Event validateAndUpdateEvent(Event event) {
    // Xác thực dữ liệu
    validateEventDates(event);

    return event;
  }

  @Override
  public void deleteEvent(Long id) {
    // Kiểm tra ID phải lớn hơn 0
    IdValidator.validateId(id, "Event");

    Event event = getEventById(id);

    // Soft delete: Chỉ đánh dấu là đã xóa
    event.setStatus(EventStatus.DELETED.getValue());
    eventRepository.save(event);
  }

  /**
   * Xác thực ngày bắt đầu và kết thúc của sự kiện.
   */
  private void validateEventDates(Event event) {
    if (event.getStartDate() != null && event.getEndDate() != null) {
      if (event.getEndDate().isBefore(event.getStartDate())) {
        throw new BadRequestException("Ngày kết thúc không thể trước ngày bắt đầu");
      }
    }
  }

  @Override
  public void updateEventStatus(Long id, Integer status) {
    // Kiểm tra ID phải lớn hơn 0
    IdValidator.validateId(id, "Event");

    Event event = getEventById(id);

    event.setStatus(status);

    eventRepository.save(event);
  }
}
