package locser.toy.domain.model.enums;

/**
 * Đại diện cho các trạng thái có thể của một Giao dịch trao đổi.
 * - REQUESTED: Mới yêu cầu, chờ phản hồi từ chủ sở hữu
 * - ACCEPTED: Chủ sở hữu đã chấp nhận yêu cầu
 * - REJECTED: Chủ sở hữu đã từ chối yêu cầu
 * - CONFIRMED: Cả hai bên đã xác nhận (sau khi ACCEPTED)
 * - SHIPPED_BY_REQUESTER: Người yêu cầu đã gửi hàng
 * - SHIPPED_BY_OWNER: Chủ sở hữu đã gửi hàng
 * - COMPLETED: Hoàn thành (cả hai xác nhận nhận hàng)
 * - CANCELLED: Bị hủy (trước khi hoàn thành)
 * - DISPUTED: Đang có tranh chấp
 */
public enum ExchangeStatus {
    REQUESTED,           // 0
    ACCEPTED,            // 1
    REJECTED,            // 2
    CONFIRMED,           // 3
    SHIPPED_BY_REQUESTER,// 4
    SHIPPED_BY_OWNER,    // 5
    COMPLETED,           // 6
    CANCELLED,           // 7
    DISPUTED             // 8
}
