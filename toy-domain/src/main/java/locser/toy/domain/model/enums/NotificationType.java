package locser.toy.domain.model.enums;

/**
 * Represents the possible types of a Notification.
 */
public enum NotificationType {
    NONE, // 0
    NEW_EXCHANGE_REQUEST, // 1
    REQUEST_ACCEPTED, // 2
    REQUEST_REJECTED, // 3
    EXCHANGE_CONFIRMED, // 4
    NEW_MESSAGE, // 5
    CAMPAIGN_STARTING, // 6
    CAMPAIGN_ENDING, // 7
    EXCHANGE_SHIPPED, // 8
    EXCHANGE_COMPLETED, // 9
    DISPUTE_FILED, // 10
    DISPUTE_UPDATED, // 11
    REVIEW_RECEIVED // 12
}
