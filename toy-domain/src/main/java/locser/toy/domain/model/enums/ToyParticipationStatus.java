package locser.toy.domain.model.enums;

/**
 * Represents the possible states of a Toy Participation.
 */
public enum ToyParticipationStatus implements ValueEnum {
    CLAIMED(1),
    DELIVERED(2),
    CANCELLED(3);

    private final int value;

    ToyParticipationStatus(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    /**
     * Find a ToyParticipationStatus by its numeric value.
     *
     * @param value The numeric value to search for
     * @return The ToyParticipationStatus with the given value, or null if not found
     */
    public static ToyParticipationStatus fromValue(int value) {
        for (ToyParticipationStatus status : ToyParticipationStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }
}
