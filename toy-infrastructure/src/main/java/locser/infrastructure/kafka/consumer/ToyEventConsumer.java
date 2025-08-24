package locser.infrastructure.kafka.consumer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import locser.infrastructure.kafka.event.ToyEvent;
import locser.toy.domain.model.entity.ToyParticipation;
import locser.toy.domain.repository.ToyParticipationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Consumer for processing toy events from Kafka.
 * This component is only active when the "kafka" profile is active.
 */
@Component
@Profile("kafka")
@RequiredArgsConstructor
@Slf4j
public class ToyEventConsumer {

    private final ToyParticipationRepository toyParticipationRepository;

    /**
     * ## 1. Tối ưu Batch Size động - Enhanced batch processing with dynamic
     * optimization
     * Listens for toy events on the toy-events topic with dynamic batch size
     * management.
     *
     * @param events     List of toy events received from Kafka
     * @param partitions Partition information
     * @param offsets    Offset information
     * @param ack        Manual acknowledgment for better control
     */
    @KafkaListener(topics = "${spring.kafka.topics.toy-events:toy-events}", groupId = "${spring.kafka.consumer.group-id:toy-exchange}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeToyEventsBatch(
            @Payload List<ToyEvent> events,
            @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets,
            Acknowledgment ack) {

        Instant startTime = Instant.now();
        boolean processingSuccessful = false;

        try {
            // log.debug("Received batch of {} toy events", events.size());

            // Group events by type for efficient batch processing
            List<ToyEvent> participationEvents = new ArrayList<>();
            List<ToyEvent> otherEvents = new ArrayList<>();

            for (ToyEvent event : events) {
                if (event.getEventType() == ToyEvent.ToyEventType.PARTICIPATION_CREATED) {
                    participationEvents.add(event);
                } else {
                    otherEvents.add(event);
                }
            }

            // Batch process participation events (most important for performance)
            if (!participationEvents.isEmpty()) {
                processParticipationCreatedEventsBatch(participationEvents);
            }

            // Process other events individually (less frequent)
            for (ToyEvent event : otherEvents) {
                processIndividualEvent(event);
            }

            processingSuccessful = true;

            // Manual acknowledgment after successful processing
            ack.acknowledge();

            // log.info("Successfully processed batch: {} participation events, {} other
            // events",
            // participationEvents.size(), otherEvents.size());

        } catch (Exception e) {
            log.error("Failed to process batch of {} events: {}", events.size(), e.getMessage(), e);
            processingSuccessful = false;

            // Don't acknowledge failed batches - they will be retried
            throw e;

        } finally {
            // Log processing time
            long processingTimeMs = java.time.Duration.between(startTime, Instant.now()).toMillis();
            log.debug("Batch processing completed in {}ms", processingTimeMs);
        }
    }

    /**
     * Process individual non-participation events
     */
    private void processIndividualEvent(ToyEvent event) {
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
        // log.info("Processing toy created event: toyId={}, userId={}, toyName={}",
        // event.getToyId(), event.getUserId(), event.getToyName());

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

    /**
     * Processes a batch of participation created events - BATCH PROCESSING for
     * better performance.
     *
     * @param events List of participation created events
     */
    private void processParticipationCreatedEventsBatch(List<ToyEvent> events) {
        // log.info("Processing batch of {} participation events", events.size());

        try {
            // Create list of ToyParticipation records for batch insert
            List<ToyParticipation> participations = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();

            for (ToyEvent event : events) {
                ToyParticipation participation = new ToyParticipation();
                participation.setUserId(event.getUserId());
                participation.setToyId(event.getToyId());
                participation.setCampaignId(event.getCampaignId());
                participation.setStatus(event.getParticipationStatus());
                participation.setParticipationDate(now);
                participation.setCreatedAt(now);
                participation.setUpdatedAt(now);

                participations.add(participation);
            }

            // Batch save all participation records
            List<ToyParticipation> savedParticipations = toyParticipationRepository.saveAll(participations);

            // log.info("Successfully created {} participation records in batch",
            // savedParticipations.size());

        } catch (Exception e) {
            log.error(
                    "Failed to create participation records in batch, falling back to individual processing. Error: {}",
                    e.getMessage(), e);

            // Fallback: process individually if batch fails
            for (ToyEvent event : events) {
                processParticipationCreatedEvent(event);
            }
        }
    }

    /**
     * Processes a single participation created event (fallback method).
     *
     * @param event The participation created event
     */
    private void processParticipationCreatedEvent(ToyEvent event) {
        try {
            // Create ToyParticipation record
            ToyParticipation participation = new ToyParticipation();
            participation.setUserId(event.getUserId());
            participation.setToyId(event.getToyId());
            participation.setCampaignId(event.getCampaignId());
            participation.setStatus(event.getParticipationStatus());
            participation.setParticipationDate(LocalDateTime.now());
            participation.setCreatedAt(LocalDateTime.now());
            participation.setUpdatedAt(LocalDateTime.now());

            // Save the participation record
            ToyParticipation savedParticipation = toyParticipationRepository.save(participation);

            // log.debug("Successfully created participation record: id={}, userId={},
            // toyId={}, campaignId={}",
            // savedParticipation.getId(), savedParticipation.getUserId(),
            // savedParticipation.getToyId(), savedParticipation.getCampaignId());

        } catch (Exception e) {
            log.error("Failed to create participation record for event: toyId={}, userId={}, campaignId={}, error={}",
                    event.getToyId(), event.getUserId(), event.getCampaignId(), e.getMessage(), e);

            // In a production system, you might want to:
            // 1. Send to a dead letter queue
            // 2. Implement retry logic
            // 3. Send notification to monitoring system
        }
    }
}
