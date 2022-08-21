package name.adibejan.string;

import name.adibejan.util.Pair;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

/**
 * String utilities.
 *
 * @author Cosmin Adrian Bejan
 * @version 1.0
 * @since JDK1.6
*/
public class StringUtil {    
  /**
   * Resticts access to the instances of this object
   */
  private StringUtil() {}

  /**
   * Splits a formated <tt>String</tt> object according to a given delimiter <tt>String</tt> object.
   * If multiple occurences of the delimiter are present, the split is perfomed 
   * at the first occurence. 
   *
   * @param rep the formated string to be split
   * @param delim the delimiter the establishes the split position
   * @throws UnsupportedStringFormatException if <code>delim</code> is not 
   * found in <code>rep</code>.
   */  
  public static Pair<String,String> split2First(String rep, String delim) {
    int pos = rep.indexOf(delim);
    if(pos == -1) throw new UnsupportedStringFormatException("["+delim+"] does not exist in ["+rep+"]");
    String first = rep.substring(0,pos);
    String second = rep.substring(pos+delim.length(), rep.length());
    return new Pair<String,String>(first,second);
  }  
}
