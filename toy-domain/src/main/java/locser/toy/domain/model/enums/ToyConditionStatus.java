package locser.toy.domain.model.enums;

/**
 * Represents the possible conditions of a Toy.
 */
public enum ToyConditionStatus {
    NEW(1), // 0
    LIKE_NEW(2), // 1
    GOOD(3), // 2
    FAIR(4), // 3
    POOR(5); // 4;

    private final int value;

    ToyConditionStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
