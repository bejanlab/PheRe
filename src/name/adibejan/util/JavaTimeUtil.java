package name.adibejan.util;

import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

/**
 * Tool class for date and time manipulation using standard java methods
 *
 * @author Cosmin Adrian Bejan
 * @version 1.0
 * @since JDK1.6
 */
public class JavaTimeUtil {

  /**
   *
   */
  private JavaTimeUtil() {};

  /**
   *
   */
  public static String getDurationFormat_HH_MM_SS(long millis) {
    return String.format("%02d:%02d:%02d", 
                         TimeUnit.MILLISECONDS.toHours(millis),
                         TimeUnit.MILLISECONDS.toMinutes(millis) -  
                         TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                         TimeUnit.MILLISECONDS.toSeconds(millis) - 
                         TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
  }
}
