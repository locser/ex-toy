package locser.toy.domain.model.enums;

/**
 * Đại diện cho các trạng thái có thể của một Tranh chấp.
 * - OPEN: Tranh chấp mới mở, chưa được xử lý
 * - UNDER_REVIEW: Đang được xem xét bởi quản trị viên
 * - RESOLVED: Đã được giải quyết
 * - CLOSED: Đã đóng tranh chấp
 */
public enum DisputeStatus {
    OPEN,           // 0
    UNDER_REVIEW,   // 1
    RESOLVED,       // 2
    CLOSED          // 3
}
