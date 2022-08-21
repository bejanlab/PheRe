package name.adibejan.util;

/**
 * Runtime expception thrown when an application fails to process an object 
 * that is expected to have a specific format.
 *
 * @author Cosmin Adrian Bejan
 * @version 1.0
 * @since JDK1.6
 */
public class UnsupportedDataFormatException extends RuntimeException {
  static final long serialVersionUID = 7898591351750359016L;
  
  /**
   * Constructs a <code>UnsupportedDataFormatException</code> with no detail message.
   */
  public UnsupportedDataFormatException() {
    super();
  }
  
  /**
   * Constructs a <code>UnsupportedDataFormatException</code> with the specified 
   * detail message. 
   *
   * @param s the detail message.
   */
  public UnsupportedDataFormatException(String s) {
    super(s);
  }
}
