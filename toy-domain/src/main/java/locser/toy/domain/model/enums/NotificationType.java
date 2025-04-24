package locser.toy.domain.model.enums;

/**
 * Đại diện cho các loại Thông báo có thể trong hệ thống.
 * - NEW_EXCHANGE_REQUEST: Yêu cầu trao đổi mới
 * - REQUEST_ACCEPTED: Yêu cầu đã được chấp nhận
 * - REQUEST_REJECTED: Yêu cầu đã bị từ chối
 * - EXCHANGE_CONFIRMED: Trao đổi đã được xác nhận
 * - NEW_MESSAGE: Tin nhắn mới
 * - CAMPAIGN_STARTING: Chiến dịch sắp bắt đầu
 * - CAMPAIGN_ENDING: Chiến dịch sắp kết thúc
 * - EXCHANGE_SHIPPED: Đồ trao đổi đã được gửi đi
 * - EXCHANGE_COMPLETED: Trao đổi đã hoàn thành
 * - DISPUTE_FILED: Tranh chấp đã được nộp
 * - DISPUTE_UPDATED: Tranh chấp đã được cập nhật
 * - REVIEW_RECEIVED: Đã nhận được đánh giá
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
