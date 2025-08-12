package locser.toy.domain.service;

/**
 * Domain service interface for publishing domain events.
 * This allows the domain layer to publish events without knowing about infrastructure details.
 */
public interface DomainEventPublisher {
    
    /**
     * Publishes a domain event.
     * 
     * @param event The domain event to publish
     */
    void publish(Object event);
}