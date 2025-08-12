package locser.infrastructure.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Event class for toy-related events to be published to Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToyEvent {
    
    /**
     * Type of the toy event.
     */
    private ToyEventType eventType;
    
    /**
     * ID of the toy.
     */
    private Long toyId;
    
    /**
     * ID of the user who owns the toy.
     */
    private Long userId;
    
    /**
     * Name of the toy.
     */
    private String toyName;
    
    /**
     * Current status of the toy.
     */
    private Integer status;
    
    /**
     * ID of the campaign the toy belongs to (if any).
     */
    private Long campaignId;
    
    /**
     * Timestamp when the event occurred.
     */
    private Instant timestamp;
    
    /**
     * Additional data related to the event (JSON format).
     */
    private String additionalData;
    
    /**
     * Participation status for participation events.
     */
    private Integer participationStatus;
    
    /**
     * Enum representing the types of toy events.
     */
    public enum ToyEventType {
        CREATED,
        UPDATED,
        DELETED,
        STATUS_CHANGED,
        ADDED_TO_CAMPAIGN,
        REMOVED_FROM_CAMPAIGN,
        PARTICIPATION_CREATED
    }
}
