package locser.toy.domain.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import locser.toy.domain.model.entity.Event;
import locser.toy.domain.repository.EventRepository;
import locser.toy.domain.service.EventDomainService;

@Service
public class EventDomainServiceImpl implements EventDomainService {


    private final EventRepository eventRepository;

    public EventDomainServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event getEventById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEventById'");
    }

    @Override
    public Event findOneById(Long id) {
        return eventRepository.findOneById(id).orElse(null);
    }

}
