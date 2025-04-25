package locser.toy.domain.model.enums;

/**
 * Represents the possible states of an Event.
 */

public enum EventStatus implements ValueEnum {
  ALL(-1),
  UPCOMING(1),
  ONGOING(2),
  FINISHED(3),
  DELETED(4);

  private final int value;

  EventStatus(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  /**
   * Find an EventStatus by its numeric value.
   *
   * @param value The numeric value to search for
   * @return The EventStatus with the given value, or null if not found
   */
  public static EventStatus fromValue(int value) {
    for (EventStatus status : EventStatus.values()) {
      if (status.getValue() == value) {
        return status;
      }
    }
    return null;
  }
}