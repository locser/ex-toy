package locser.toy.domain.service;

import locser.toy.domain.model.entity.Event;

public interface EventDomainService {
    Event getEventById(Long id);

    Event findOneById(Long id);
}
