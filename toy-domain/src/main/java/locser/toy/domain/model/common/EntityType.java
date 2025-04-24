package locser.toy.domain.model.common;

/**
 * Đại diện cho các loại thực thể có thể liên quan đến các thông báo hoặc sự kiện.
 * - EXCHANGE: Giao dịch trao đổi
 * - CAMPAIGN: Chiến dịch/Sự kiện
 * - USER: Người dùng
 * - TOY: Đồ chơi
 * - DISPUTE: Tranh chấp
 * - REVIEW: Đánh giá
 */
public enum EntityType {
    EXCHANGE,   // 0
    CAMPAIGN,   // 1
    USER,       // 2
    TOY,        // 3
    DISPUTE,    // 4
    REVIEW      // 5
}
