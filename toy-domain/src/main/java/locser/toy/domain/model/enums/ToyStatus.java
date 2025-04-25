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
    AWAITING_APPROVAL // 5
    ;

    public int getValue() {
        return this.ordinal();
    }
}
