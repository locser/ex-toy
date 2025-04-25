package locser.toy.domain.model.enums;

/**
 * Represents the possible states of an Exchange.
 */
public enum ExchangeStatus {
    REQUESTED, // 0
    ACCEPTED, // 1
    REJECTED, // 2
    CONFIRMED, // 3
    SHIPPED_BY_REQUESTER, // 4
    SHIPPED_BY_OWNER, // 5
    COMPLETED, // 6
    CANCELLED, // 7
    DISPUTED // 8
    ;

    public int getValue() {
        return this.ordinal();
    }

}
