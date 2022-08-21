package name.adibejan.util;

import static java.lang.System.out;

/**
 * Exception utils
 *
 * @author Cosmin Adrian Bejan
 * @version 1.0
 * @since JDK1.6
 */
public class ExceptionUtil {
  /**
   *
   */
  public static void trace(Exception e) {
    e.printStackTrace();
  }

  /**
   *
   */
  public static void trace(Exception e, String message) {
    out.println(message + " :" + e.getMessage());
    e.printStackTrace();
  }
}
