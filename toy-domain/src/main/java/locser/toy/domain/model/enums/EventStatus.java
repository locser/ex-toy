package locser.toy.domain.model.enums;

/**
 * Đại diện cho các trạng thái có thể của một Sự kiện.
 * - UPCOMING: Sự kiện sắp diễn ra
 * - ONGOING: Sự kiện đang diễn ra
 * - FINISHED: Sự kiện đã kết thúc
 * - DELETED: Sự kiện đã bị xóa
 */

public enum EventStatus {
    UPCOMING, // 0
    ONGOING,  // 1
    FINISHED,  // 2
    DELETED

}