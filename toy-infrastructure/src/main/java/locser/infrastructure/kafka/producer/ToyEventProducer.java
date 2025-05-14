package locser.infrastructure.kafka.producer;

import locser.infrastructure.kafka.event.ToyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * Producer for publishing toy events to Kafka.
 * This component is only active when the "kafka" profile is active.
 */
@Component
@Profile("kafka")
@RequiredArgsConstructor
@Slf4j
public class ToyEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.toy-events:toy-events}")
    private String toyEventsTopic;

    /**
     * Publishes a toy event to Kafka.
     *
     * @param event The toy event to publish
     * @return A CompletableFuture that will be completed when the send operation completes
     */
    public CompletableFuture<SendResult<String, Object>> publishToyEvent(ToyEvent event) {
        if (event.getTimestamp() == null) {
            event.setTimestamp(Instant.now());
        }

        log.info("Publishing toy event: {}", event);
        
        // Use the toy ID as the key for partitioning
        String key = event.getToyId().toString();
        
        // Send the event to Kafka
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(toyEventsTopic, key, event);
        
        // Add callback for logging
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent toy event=[{}] with offset=[{}]", event, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send toy event=[{}] due to : {}", event, ex.getMessage(), ex);
            }
        });
        
        return future;
    }

    /**
     * Creates and publishes a toy created event.
     *
     * @param toyId    ID of the created toy
     * @param userId   ID of the user who created the toy
     * @param toyName  Name of the created toy
     * @return A CompletableFuture that will be completed when the send operation completes
     */
    public CompletableFuture<SendResult<String, Object>> publishToyCreatedEvent(Long toyId, Long userId, String toyName) {
        ToyEvent event = ToyEvent.builder()
                .eventType(ToyEvent.ToyEventType.CREATED)
                .toyId(toyId)
                .userId(userId)
                .toyName(toyName)
                .timestamp(Instant.now())
                .build();
        
        return publishToyEvent(event);
    }

    /**
     * Creates and publishes a toy status changed event.
     *
     * @param toyId    ID of the toy
     * @param userId   ID of the user who owns the toy
     * @param toyName  Name of the toy
     * @param status   New status of the toy
     * @return A CompletableFuture that will be completed when the send operation completes
     */
    public CompletableFuture<SendResult<String, Object>> publishToyStatusChangedEvent(Long toyId, Long userId, String toyName, Integer status) {
        ToyEvent event = ToyEvent.builder()
                .eventType(ToyEvent.ToyEventType.STATUS_CHANGED)
                .toyId(toyId)
                .userId(userId)
                .toyName(toyName)
                .status(status)
                .timestamp(Instant.now())
                .build();
        
        return publishToyEvent(event);
    }
}
