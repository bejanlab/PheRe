package name.adibejan.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import java.io.Serializable;

/**
 * Tool class for an (Object, Object) pair.
 *
 * @author Cosmin Adrian Bejan
 * @version 1.0
 * @since JDK1.6
 */
public class Pair<A,B> implements Serializable {  
  private static final long serialVersionUID = 11L;
  
  private A first;
  private B second;
  
  /**
   * Builds a <code>Pair</code> object from two objects of types A and B
   */
  public Pair(A first, B second) {
    setFirst(first);
    setSecond(second);
  }

  /**
   * Sets the first object
   *
   * @param first the new first object of this pair
   */
  public void setFirst(A first) {
    this.first = first;
  }

  /**
   * Get the first object
   *
   * @return the first object of this pair
   */
  public A getFirst() {
    return first;
  }

  /**
   *
   */
  public static <A, B> List<A> getFirst(List<Pair<A,B>> pairList) {
    List<A> firsList = new ArrayList<A>();
    
    for(Pair<A,B> pair : pairList)
      firsList.add(pair.getFirst());
    
    return firsList;
  }
  
  /**
   * Sets the second object
   *
   * @param second the new second object of this pair
   */
  public void setSecond(B second) {
    this.second = second;
  }
  
  /**
   * Get the second object
   *
   * @return the second object of this pair
   */
  public B getSecond() {
    return second;
  }

  /**
   * The equals method for Pair objects
   *
   */
  @Override
  public boolean equals(Object that) {
    if (this == that ) return true;
    if (!(that instanceof Pair)) return false;
    Pair thatpair = (Pair)that;
    return (first == null ? thatpair.first == null : first.equals(thatpair.first)) &&
      (second == null ? thatpair.second == null : second.equals(thatpair.second));
  }

  /**
   * The hasCode method for Pair objects
   *
   */
  @Override
  public int hashCode() {
    return (((first == null) ? 0 : first.hashCode()) << 16) ^ 
	((second == null) ? 0: second.hashCode());

    //alternative:   return ((first+second)*(first+second) + 3*first + second)/2 ;
  }  

  /**
   * The string format of a Pair object
   *
   * @return the string representation of a Pair
   */
  @Override
  public String toString() {
    //return "<"+first+"|"+second+">";
    return toString(" ");
  }

  public String toString(String delim) {
    return first.toString() + delim + second.toString();
  }
}
