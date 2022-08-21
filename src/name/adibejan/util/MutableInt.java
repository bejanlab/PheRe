package name.adibejan.util;

import java.util.Map;
import java.util.Comparator;


/**
 * Wrapper for int primitives that supports performing aritmetic features in place
 *
 * @author Cosmin Adrian Bejan
 * @version 1.0
 * @since JDK1.6
 */
public class MutableInt implements Comparable<MutableInt>, java.io.Serializable {
  static final long serialVersionUID = -35367857166L;  //not generated
    
  private int value;
  
  /**
   * Creates a new instance with default value on 0
   */
  public MutableInt() {
    value = 0;
  }

  /**
   * Creates a new instance with a specified int value
   */
  public MutableInt(int value) {
    this.value = value;
  }
  
  /**
   * Increment the int value by 1
   *
   */
  public void inc() {
    value++;
  }

  public void inc(int step) {
    value += step;
  }

  @Override
  public int compareTo(MutableInt other) {
    if( value < other.value) return -1;
    if( value > other.value) return 1;
    return 0;
  }
  
  public void set(int value) {
    this.value = value;
  }

  public int get() {
    return value;
  }
  
  @Override
  public String toString() {
    return ""+value;
  }

  /**
   * Descending comparator based on (int, string) values
   */
  public static Comparator<Map.Entry<String, MutableInt>> getDescComparatorByValueKey() {    
    return new Comparator<Map.Entry<String, MutableInt>>() {
      public int compare(Map.Entry<String, MutableInt> c1, Map.Entry<String, MutableInt> c2) {        
        if(c2.getValue().get() < c1.getValue().get()) return -1;
        else if(c2.getValue().get() > c1.getValue().get()) return 1;
        else return c2.getKey().compareTo(c1.getKey());        
      }
    };
  }

  /**
   * Descending comparator based on int values
   */
  public static Comparator<Map.Entry<String, MutableInt>> getDescComparatorByValue() {    
    return new Comparator<Map.Entry<String, MutableInt>>() {
      public int compare(Map.Entry<String, MutableInt> c1, Map.Entry<String, MutableInt> c2) {
        return c2.getValue().compareTo(c1.getValue());
      }
    };
  }

  /**
   * Ascending comparator based on (int, string) values
   */
  public static Comparator<Map.Entry<String, MutableInt>> getAscComparatorByValueKey() {
    return new Comparator<Map.Entry<String, MutableInt>>() {
      public int compare(Map.Entry<String, MutableInt> c1, Map.Entry<String, MutableInt> c2) {        
        if(c1.getValue().get() < c2.getValue().get()) return -1;
        else if(c1.getValue().get() > c2.getValue().get()) return 1;
        else return c1.getKey().compareTo(c2.getKey());        
      }
    };
  }

  /**
   * Ascending comparator based on int values
   */
  public static Comparator<Map.Entry<String, MutableInt>> getAscComparatorByValue() {    
    return new Comparator<Map.Entry<String, MutableInt>>() {
      public int compare(Map.Entry<String, MutableInt> c1, Map.Entry<String, MutableInt> c2) {
        return c1.getValue().compareTo(c2.getValue());
      }
    };
  }
}
