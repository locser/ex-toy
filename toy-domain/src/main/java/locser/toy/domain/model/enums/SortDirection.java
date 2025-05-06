package locser.toy.domain.model.enums;

/**
 * Enum đại diện cho hướng sắp xếp.
 */
public enum SortDirection {
    ASC, DESC;

    public static SortDirection fromString(String direction) {
        if (direction == null || direction.isEmpty()) {
            return DESC; // Mặc định là giảm dần
        }

        try {
            return SortDirection.valueOf(direction.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ASC; // Nếu không hợp lệ, mặc định là tăng dần
        }
    }
}
