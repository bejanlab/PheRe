package name.adibejan.string;

import name.adibejan.util.UnsupportedDataFormatException;

/**
 * Runtime expception thrown when an application fails to process a string
 * that is expected to have a specific format.
 *
 * @author Cosmin Adrian Bejan
 * @version 1.0
 * @since JDK1.6
 */
public class UnsupportedStringFormatException extends UnsupportedDataFormatException {
  /**
   * Constructs a <code>UnsupportedStringFormatException</code> with no detail message.
   */
  public UnsupportedStringFormatException() {
    super();
  }

  /**
   * Constructs a <code>UnsupportedStringFormatException</code> with the specified 
   * detail message. 
   *
   * @param s the detail message.
   */
  public UnsupportedStringFormatException(String s) {
    super(s);
  }
}
