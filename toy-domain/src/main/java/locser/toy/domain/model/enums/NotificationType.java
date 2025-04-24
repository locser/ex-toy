package locser.toy.domain.model.enums;

/**
 * Represents the possible types of a Notification.
 */
public enum NotificationType {
    NEW_EXCHANGE_REQUEST,  // 0
    REQUEST_ACCEPTED,      // 1
    REQUEST_REJECTED,      // 2
    EXCHANGE_CONFIRMED,    // 3
    NEW_MESSAGE,           // 4
    CAMPAIGN_STARTING,     // 5
    CAMPAIGN_ENDING,       // 6
    EXCHANGE_SHIPPED,      // 7
    EXCHANGE_COMPLETED,    // 8
    DISPUTE_FILED,         // 9
    DISPUTE_UPDATED,       // 10
    REVIEW_RECEIVED        // 11
}
