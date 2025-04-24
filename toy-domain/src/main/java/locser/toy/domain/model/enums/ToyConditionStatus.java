package locser.toy.domain.model.enums;

/**
 * Đại diện cho các tình trạng có thể của một Đồ chơi.
 * - NEW: Mới hoàn toàn, chưa sử dụng
 * - LIKE_NEW: Như mới, đã sử dụng rất ít
 * - GOOD: Tình trạng tốt, có dấu hiệu sử dụng nhẹ
 * - FAIR: Tình trạng khá, có dấu hiệu sử dụng rõ ràng
 * - POOR: Tình trạng kém, có nhiều dấu hiệu hư hỏng
 */
public enum ToyConditionStatus {
    NEW,        // 0
    LIKE_NEW,   // 1
    GOOD,       // 2
    FAIR,       // 3
    POOR        // 4
}
