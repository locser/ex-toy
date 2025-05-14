package locser.infrastructure.kafka.consumer;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import locser.infrastructure.kafka.event.ToyEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Consumer for processing toy events from Kafka.
 * This component is only active when the "kafka" profile is active.
 */
@Component
@Profile("kafka")
@Slf4j
public class ToyEventConsumer {

    /**
     * Listens for toy events on the toy-events topic.
     *
     * @param event The toy event received from Kafka
     */
    @KafkaListener(topics = "${spring.kafka.topics.toy-events:toy-events}", groupId = "${spring.kafka.consumer.group-id:toy-exchange}")
    public void consumeToyEvent(ToyEvent event) {
        log.info("Received toy event: {}", event);

        // Process the event based on its type
        switch (event.getEventType()) {
            case CREATED:
                processToyCreatedEvent(event);
                break;
            case UPDATED:
                processToyUpdatedEvent(event);
                break;
            case DELETED:
                processToyDeletedEvent(event);
                break;
            case STATUS_CHANGED:
                processToyStatusChangedEvent(event);
                break;
            case ADDED_TO_CAMPAIGN:
                processToyAddedToCampaignEvent(event);
                break;
            case REMOVED_FROM_CAMPAIGN:
                processToyRemovedFromCampaignEvent(event);
                break;
            default:
                log.warn("Unknown toy event type: {}", event.getEventType());
        }
    }

    /**
     * Processes a toy created event.
     *
     * @param event The toy created event
     */
    private void processToyCreatedEvent(ToyEvent event) {
        log.info("Processing toy created event: toyId={}, userId={}, toyName={}",
                event.getToyId(), event.getUserId(), event.getToyName());

        // Implement business logic for toy creation event
        // For example, update statistics, send notifications, etc.
    }

    /**
     * Processes a toy updated event.
     *
     * @param event The toy updated event
     */
    private void processToyUpdatedEvent(ToyEvent event) {
        log.info("Processing toy updated event: toyId={}, userId={}, toyName={}",
                event.getToyId(), event.getUserId(), event.getToyName());

        // Implement business logic for toy update event
    }

    /**
     * Processes a toy deleted event.
     *
     * @param event The toy deleted event
     */
    private void processToyDeletedEvent(ToyEvent event) {
        log.info("Processing toy deleted event: toyId={}, userId={}",
                event.getToyId(), event.getUserId());

        // Implement business logic for toy deletion event
    }

    /**
     * Processes a toy status changed event.
     *
     * @param event The toy status changed event
     */
    private void processToyStatusChangedEvent(ToyEvent event) {
        log.info("Processing toy status changed event: toyId={}, userId={}, status={}",
                event.getToyId(), event.getUserId(), event.getStatus());

        // Implement business logic for toy status change event
    }

    /**
     * Processes a toy added to campaign event.
     *
     * @param event The toy added to campaign event
     */
    private void processToyAddedToCampaignEvent(ToyEvent event) {
        log.info("Processing toy added to campaign event: toyId={}, userId={}, campaignId={}",
                event.getToyId(), event.getUserId(), event.getCampaignId());

        // Implement business logic for toy added to campaign event
    }

    /**
     * Processes a toy removed from campaign event.
     *
     * @param event The toy removed from campaign event
     */
    private void processToyRemovedFromCampaignEvent(ToyEvent event) {
        log.info("Processing toy removed from campaign event: toyId={}, userId={}, campaignId={}",
                event.getToyId(), event.getUserId(), event.getCampaignId());

        // Implement business logic for toy removed from campaign event
    }
}
