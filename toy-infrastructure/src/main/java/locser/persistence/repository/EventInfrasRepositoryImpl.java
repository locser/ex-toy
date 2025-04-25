package locser.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import locser.persistence.mapper.EventJPAMapper;
import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.enums.EventStatus;
import locser.toy.domain.repository.EventRepository;

@Service
public class EventInfrasRepositoryImpl implements EventRepository {

  private final EventJPAMapper eventJPAMapper;

  EventInfrasRepositoryImpl(EventJPAMapper eventJPAMapper) {
    this.eventJPAMapper = eventJPAMapper;
  }

  @Override
  public Optional<Event> findOneById(Long id) {
    // return eventJPAMapper.findById(id);
    return this.eventJPAMapper.findOneById(id);
  }

  @Override
  public Event save(Event event) {
    return eventJPAMapper.save(event);
  }

  @Override
  public Event findById(Long id) {
    return eventJPAMapper.findById(id).orElse(null);
  }

  @Override
  public List<Event> findByStatus(int status) {
    return eventJPAMapper.findByStatus(status);
  }

  @Override
  public List<Event> findAll() {
    return eventJPAMapper.findAll();
  }

  @Override
  public List<Event> findWithPagination(int page, int size, int status) {

    Pageable pageable = PageRequest.of(page, size);

    if (status != EventStatus.ALL.getValue()) {
      return eventJPAMapper.findByStatus(status, pageable).getContent();
    } else {
      return eventJPAMapper.findAll(pageable).getContent();
    }
  }

  @Override
  public long count(int status) {
    if (status != EventStatus.ALL.getValue()) {
      return eventJPAMapper.countByStatus(status);
    } else {
      return eventJPAMapper.count();
    }
  }
}