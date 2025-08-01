package locser.toy.domain.model.enums;


/**
 * Interface for enums with numeric values. Any enum that has a numeric value should implement this
 * interface.
 */
public interface ValueEnum {

  /**
   * Get the numeric value of the enum constant.
   *
   * @return The numeric value
   */
  int getValue();
}