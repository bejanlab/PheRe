package name.adibejan.util;

import static java.lang.System.out;

/**
 * (Singleton) Stopwatch
 *
 * @author Cosmin Adrian Bejan
 * @version 1.0
 * @since JDK1.7, February 2014
 */
public class Stopwatch {
  /* the singleton instance */
  private static Stopwatch instance = null;
  private long startTime = System.currentTimeMillis();    
  
  /**
   * Prevents creating multiple instances of this class.
   */
  private Stopwatch() {}
  
  /**
   * Stopwatch follows a singleton pattern and therefore, it will create only one instance if used. 
   *
   * @return a handler to Stopwatch
   */
  public static synchronized Stopwatch getInstance() {
    if(instance == null) {
      instance = new Stopwatch();
      instance.restart();
    }
    return instance;
  }

  /**
   * Restarts the chronograph
   */
  public void restart() {
    startTime = System.currentTimeMillis();
  }
  
  /**
   * Returns the elapsed period since the last restart of the stopwatch
   */
  public long getDuration() {
    return System.currentTimeMillis() - startTime;
  }
}