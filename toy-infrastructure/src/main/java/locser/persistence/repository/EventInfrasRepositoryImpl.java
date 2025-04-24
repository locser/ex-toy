package locser.persistence.repository;

import locser.persistence.mapper.EventJPAMapper;
import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.enums.EventStatus;
import locser.toy.domain.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Triển khai của EventRepository interface sử dụng JPA để truy cập cơ sở dữ liệu.
 */
@Service
public class EventInfrasRepositoryImpl implements EventRepository {

    private final EventJPAMapper eventJPAMapper;

    public EventInfrasRepositoryImpl(EventJPAMapper eventJPAMapper) {
        this.eventJPAMapper = eventJPAMapper;
    }

    @Override
    public Optional<Event> findById(Long id) {
        return this.eventJPAMapper.findById(id);
    }

    @Override
    public Optional<Event> findOneById(Long id) {
        return findById(id);
    }

    @Override
    public List<Event> findAll() {
        return this.eventJPAMapper.findAll();
    }

    @Override
    public List<Event> findByStatus(EventStatus status) {
        return this.eventJPAMapper.findByStatus(status);
    }

    @Override
    public Event save(Event event) {
        return this.eventJPAMapper.save(event);
    }

    @Override
    public void delete(Event event) {
        this.eventJPAMapper.delete(event);
    }
}
