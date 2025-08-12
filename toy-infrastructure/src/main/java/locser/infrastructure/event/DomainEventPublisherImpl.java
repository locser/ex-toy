package locser.infrastructure.event;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import locser.infrastructure.kafka.producer.ToyEventProducer;
import locser.toy.domain.model.entity.ToyParticipation;
import locser.toy.domain.model.event.ParticipationCreatedEvent;
import locser.toy.domain.repository.ToyParticipationRepository;
import locser.toy.domain.service.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;

/**
 * Infrastructure implementation of DomainEventPublisher.
 * This class handles the translation between domain events and infrastructure
 * concerns (Kafka).
 * It follows DDD principles by keeping infrastructure logic separate from
 * domain logic.
 */
@Service
@Slf4j
public class DomainEventPublisherImpl implements DomainEventPublisher {

    /**
     * │ Nguyên nhân:
     * │
     * │ • Bạn đã đổi active: kafka → active: default trong application.yml
     * │ • Khi profile "kafka" không active → Spring không tạo bean ToyEventProducer
     * │
     * │ • @Autowired(required = false) → toyEventProducer = null
     * │ • Check if (toyEventProducer != null) → FALSE → "Kafka not available"
     */
    @Autowired(required = false)
    private ToyEventProducer toyEventProducer;

    private final ToyParticipationRepository toyParticipationRepository;

    public DomainEventPublisherImpl(ToyParticipationRepository toyParticipationRepository) {
        this.toyParticipationRepository = toyParticipationRepository;
    }

    @Override
    public void publish(Object event) {
        if (event instanceof ParticipationCreatedEvent) {
            handleParticipationCreatedEvent((ParticipationCreatedEvent) event);
        } else {
            log.warn("Unknown domain event type: {}", event.getClass().getSimpleName());
        }
    }

    private void handleParticipationCreatedEvent(ParticipationCreatedEvent event) {
        log.info("Handling ParticipationCreatedEvent: {}", event);

        // Try to send via Kafka if available
        if (toyEventProducer != null) {
            try {
                toyEventProducer.publishParticipationCreatedEvent(
                        event.getToyId(),
                        event.getUserId(),
                        event.getCampaignId(),
                        event.getParticipationStatus()).whenComplete((result, ex) -> {
                            if (ex == null) {
                                log.info(
                                        "Successfully sent participation event to Kafka: toyId={}, userId={}, campaignId={}",
                                        event.getToyId(), event.getUserId(), event.getCampaignId());
                            } else {
                                log.error(
                                        "Failed to send participation event to Kafka, falling back to direct DB save: toyId={}, userId={}, campaignId={}, error={}",
                                        event.getToyId(), event.getUserId(), event.getCampaignId(), ex.getMessage());
                                // Fallback to direct database save
                                createParticipationDirectly(event);
                            }
                        });
                System.out.println("handleParticipationCreatedEvent Sent participation event to Kafka");
            } catch (Exception e) {
                log.error(
                        "Exception while sending to Kafka, falling back to direct DB save: toyId={}, userId={}, campaignId={}, error={}",
                        event.getToyId(), event.getUserId(), event.getCampaignId(), e.getMessage());
                // Fallback to direct database save
                createParticipationDirectly(event);
            }
        } else {
            log.info("Kafka not available, creating participation record directly: toyId={}, userId={}, campaignId={}",
                    event.getToyId(), event.getUserId(), event.getCampaignId());
            // Kafka is not available, create participation record directly
            System.out.println("Kafka not available, creating participation record directly NOT KAFKA");
            createParticipationDirectly(event);
        }
    }

    private void createParticipationDirectly(ParticipationCreatedEvent event) {
        try {
            ToyParticipation participation = new ToyParticipation();
            participation.setUserId(event.getUserId());
            participation.setToyId(event.getToyId());
            participation.setCampaignId(event.getCampaignId());
            participation.setStatus(event.getParticipationStatus());
            participation.setParticipationDate(LocalDateTime.now());
            participation.setCreatedAt(LocalDateTime.now());
            participation.setUpdatedAt(LocalDateTime.now());

            ToyParticipation savedParticipation = toyParticipationRepository.save(participation);
            log.info("Successfully created participation record directly: id={}, toyId={}, userId={}, campaignId={}",
                    savedParticipation.getId(), savedParticipation.getToyId(),
                    savedParticipation.getUserId(), savedParticipation.getCampaignId());
        } catch (Exception e) {
            log.error("Failed to create participation record directly: toyId={}, userId={}, campaignId={}, error={}",
                    event.getToyId(), event.getUserId(), event.getCampaignId(), e.getMessage(), e);
            // In production, you might want to:
            // 1. Send to a dead letter queue
            // 2. Implement retry logic
            // 3. Send notification to monitoring system
            throw new RuntimeException("Failed to create participation record", e);
        }
    }
}