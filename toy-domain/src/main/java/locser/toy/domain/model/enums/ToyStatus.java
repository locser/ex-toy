package locser.toy.domain.model.enums;

/**
 * Represents the possible states of a Toy.
 */
public enum ToyStatus {
    AVAILABLE, // 0
    PENDING_EXCHANGE, // 1
    IN_EXCHANGE, // 2
    EXCHANGED, // 3
    REMOVED, // 4
    AWAITING_APPROVAL, // 5
    GIVEAWAY_AVAILABLE, // 6 - Available for giveaway campaign
    GIVEAWAY_CLAIMED // 7 - Claimed in giveaway campaign
    ;

    public int getValue() {
        return this.ordinal();
    }
}
