package locser.toy.domain.model.enums;

/**
 * Represents the possible states of a Toy.
 */
public enum ToyStatus {
    AVAILABLE(1), // 1
    PENDING_EXCHANGE(2), // 2
    IN_EXCHANGE(3), // 3
    EXCHANGED(4), // 4
    REMOVED(5), // 5
    AWAITING_APPROVAL(6), // 6
    GIVEAWAY_AVAILABLE(7), // 7 - Available for giveaway campaign
    GIVEAWAY_CLAIMED(8); // 8 - Claimed in giveaway campaign

    private final int value;

    ToyStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
