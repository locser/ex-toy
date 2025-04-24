package locser.toy.domain.model.enums;

/**
 * Represents the possible states of an Event.
 */

public enum EventStatus {
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
}