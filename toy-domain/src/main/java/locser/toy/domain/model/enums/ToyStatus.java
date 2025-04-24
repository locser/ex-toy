package locser.toy.domain.model.enums;

/**
 * Đại diện cho các trạng thái có thể của một Đồ chơi.
 * - AVAILABLE: Có sẵn để trao đổi
 * - PENDING_EXCHANGE: Đang chờ xử lý yêu cầu trao đổi
 * - IN_EXCHANGE: Đang trong quá trình trao đổi
 * - EXCHANGED: Đã được trao đổi thành công
 * - REMOVED: Đã bị xóa khỏi hệ thống
 * - AWAITING_APPROVAL: Đang chờ phê duyệt
 */
public enum ToyStatus {
    AVAILABLE,          // 0
    PENDING_EXCHANGE,   // 1
    IN_EXCHANGE,        // 2
    EXCHANGED,          // 3
    REMOVED,            // 4
    AWAITING_APPROVAL   // 5
}
