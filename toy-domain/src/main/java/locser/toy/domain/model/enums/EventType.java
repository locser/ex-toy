package locser.toy.domain.model.enums;

/**
 * Represents the possible types of an Event.
 */
public enum EventType implements ValueEnum {
    EXCHANGE(1),
    GIVEAWAY(2);

    private final int value;

    EventType(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    /**
     * Find an EventType by its numeric value.
     *
     * @param value The numeric value to search for
     * @return The EventType with the given value, or null if not found
     */
    public static EventType fromValue(int value) {
        for (EventType type : EventType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
