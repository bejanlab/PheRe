package name.adibejan.util;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.decorator.TIntObjectMapDecorator;
import gnu.trove.list.array.TIntArrayList;

import static java.lang.System.out;

/**
 * Data structure that keeps an efficient mapping between objects (items) and ints (indexes)
 * item -- (index, count)
 *
 * @author Cosmin Adrian Bejan
 * @version 1.0
 * @since JDK1.6, February 2011
 */
public class IntCounterHashtable implements java.io.Serializable {
  private static final long serialVersionUID = 77434675247L;
  private TIntObjectHashMap<MutableInt> map;

  /**
   * Builds the map of keys
   */
  public IntCounterHashtable() {
    map = new TIntObjectHashMap<MutableInt>();
  }
  
  /**
   * Tests whether a specified object is in the map
   */
  public boolean contains(int key) {
    return map.containsKey(key);
  }
  
  /**
   * Updates the counts for a specified object. 
   * 
   * The default start count is set to 1.
   */
  public void update(int key) {
    update(key, 1, 1);
  }
  
  /**
   * Updates the counts for a specified object <code>key</code> with a specified <code>increment</code>. 
   * If the <code>key</code> is not found in the map, the inital counter value is set to <code>start</code>.
   *
   * TODO: [refactor]  modified update(int key, int startCounter) which is always incremented by 1
   */
  public void update(int key, int increment, int start) {
    if(map.containsKey(key))
      map.get(key).inc(increment);
    else 
      map.put(key, new MutableInt(start));
  }

  /**
   * Update the counts from another map   
   *
   */
  public void update(IntCounterHashtable other) {
    for(Map.Entry<Integer, MutableInt> otherEntry : other.getSetEntries())
      update(otherEntry.getKey(), otherEntry.getValue().get(), otherEntry.getValue().get());
  }
  
  /**
   * Assigns to the map a specific (key, value) pair
   *
   */
  public void put(int key, int value) {
    if(map.containsKey(key))
      map.get(key).set(value);
    else 
      map.put(key, new MutableInt(value));
  }
  
  /**
   * Returns the frequency of a given object
   *
   * @return the count of a specified object. If the event is not in the hashtable, then it
   * will return 0
   */
  public int getCount(int key) {
    if(!map.containsKey(key)) return 0;
    return map.get(key).get();
  }

  /**
   * @return the sum over all values
   *
   */
  public int getSumCounts() {
    int sum = 0;
    for(TIntObjectIterator<MutableInt> it = map.iterator(); it.hasNext(); ) {
      it.advance();      
      sum += it.value().get();
    }
    return sum; 
  }

  /**
   * @return max count
   */
  public int getMaxCount() {
    int max = Integer.MIN_VALUE;
    for(TIntObjectIterator<MutableInt> it = map.iterator(); it.hasNext(); ) {
      it.advance();
      if(max < it.value().get())
        max = it.value().get();
    }
    return max; 
  }

  /**
   * @return the a list wit all the keys having a specific value 
   */
  public TIntArrayList getMatchingKeys(int count) {
    TIntArrayList list = new TIntArrayList();
    for(TIntObjectIterator<MutableInt> it = map.iterator(); it.hasNext(); ) {
      it.advance();
      if(count == it.value().get())
        list.add(it.key());
    }
    
    return list;
  }
  
  /**
   * Returns the keys of this hashtable
   *
   */
  public int[] keys() {
    return map.keys();
  }

  /*************************************************************************** get entries
   *
   */
  public List<Map.Entry<Integer, MutableInt>> getEntriesDescSortedByCounters() {
    List<Map.Entry<Integer, MutableInt>> entries = getListEntries();
    Collections.sort(entries, getDescComparatorByValue());
    
    return entries;
  }

  public Set<Map.Entry<Integer, MutableInt>> getSetEntries() {
    return new TIntObjectMapDecorator<MutableInt>(map).entrySet();
  }
  
  public List<Map.Entry<Integer, MutableInt>> getListEntries() {
    return new ArrayList<Map.Entry<Integer, MutableInt>>(getSetEntries());
  }

  
  /****************************************************************************** prints
   *
   */
  public void print() {
    int[] keys = map.keys();
    for(int i = 0; i < keys.length; i++) 
      out.println(keys[i]+" "+map.get(keys[i]).get());    
  }

  public void printKeysSorted() {
    int[] keys = map.keys();
    Arrays.sort(keys);
    for(int i = 0; i < keys.length; i++) 
      out.println(keys[i]+" "+map.get(keys[i]).get());    
  }

  public void printKeysReverseSorted() {
    int[] keys = map.keys();
    Arrays.sort(keys);
    for(int i = keys.length - 1; i >= 0; i--) 
      out.println(keys[i]+" "+map.get(keys[i]).get());    
  }

  public void printCountersAscSortedByKey() {
    List<Map.Entry<Integer, MutableInt>> entries = getListEntries();
    Collections.sort(entries, getAscComparatorByKey());
    for(Map.Entry<Integer, MutableInt> entry : entries) {
      out.println(entry.getKey().toString() + " " + entry.getValue().get());
    }
  }
  
  public void printCountersSorted() {
    List<Map.Entry<Integer, MutableInt>> entries = getListEntries();
    Collections.sort(entries, getDescComparatorByValue());
    for(Map.Entry<Integer, MutableInt> entry : entries) {
      out.println(entry.getKey().toString() + " " + entry.getValue().get());
    }
  }

  public void printCountersReverseSorted() {
    List<Map.Entry<Integer, MutableInt>> entries = getListEntries();
    Collections.sort(entries, getAscComparatorByValue());
    for(Map.Entry<Integer, MutableInt> entry : entries) {
      out.println(entry.getKey().toString() + " " + entry.getValue().get());
    }
  }

  /******************************************************************** comparators
   *
   */
  public static Comparator<Map.Entry<Integer, MutableInt>> getAscComparatorByKey() {
    return new Comparator<Map.Entry<Integer, MutableInt>>() {
      public int compare(Map.Entry<Integer, MutableInt> c1, Map.Entry<Integer, MutableInt> c2) {
        return c1.getKey().compareTo(c2.getKey());
      }
    };
  }

  public static Comparator<Map.Entry<Integer, MutableInt>> getAscComparatorByValue() {
    return new Comparator<Map.Entry<Integer, MutableInt>>() {
      public int compare(Map.Entry<Integer, MutableInt> c1, Map.Entry<Integer, MutableInt> c2) {
        return c1.getValue().compareTo(c2.getValue());
      }
    };
  }

  public static Comparator<Map.Entry<Integer, MutableInt>> getDescComparatorByKey() {
    return new Comparator<Map.Entry<Integer, MutableInt>>() {
      public int compare(Map.Entry<Integer, MutableInt> c1, Map.Entry<Integer, MutableInt> c2) {
        return c2.getKey().compareTo(c1.getKey());
      }
    };
  }
  
  public static Comparator<Map.Entry<Integer, MutableInt>> getDescComparatorByValue() {
    return new Comparator<Map.Entry<Integer, MutableInt>>() {
      public int compare(Map.Entry<Integer, MutableInt> c1, Map.Entry<Integer, MutableInt> c2) {
        return c2.getValue().compareTo(c1.getValue());
      }
    };
  }
}
