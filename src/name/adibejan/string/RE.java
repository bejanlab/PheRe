package name.adibejan.string;

import name.adibejan.util.Pair;

import java.util.Comparator;
import java.util.regex.Pattern;

import static java.lang.System.out;

/**
 * Regular expression structure for phenotype searching
 *
 * @author Cosmin Adrian Bejan
 * @version 1.0
 * @since JDK1.8 | March 2019
 */
public class RE {
  private int id;
  private String re;
  private Pattern pattern; 
  private String matchType; // c = case sensitive; i = case insensitive
  
  
  /**
   * input: line in a rule file
   * flag delim RE
   */
  public static RE getInstance(int id, String line, String delim) {
    Pair<String, String> pair = StringUtil.split2First(line, delim);
    if(!pair.getFirst().equals("c") && !pair.getFirst().equals("i"))
      throw new RuntimeException("Invalid match type ["+pair.getFirst()+"]");
    
    RE rule = new RE();
    rule.matchType = pair.getFirst();
    rule.re = pair.getSecond();
    rule.id = id;
    
    if(rule.matchType.equals("c"))
      rule.pattern = Pattern.compile(rule.re, Pattern.MULTILINE);
    else
      rule.pattern = Pattern.compile(rule.re, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
    
    return rule;
  }

  /**
   *
   */
  public void print01() {
    out.println("RE["+id+"]["+re+"]["+matchType+"]");
  }
  
  /**
   *
   */
  public int getId() {
    return id;
  }
  
  /**
   *
   */
  public Pattern getPattern() {
    return pattern;
  }
  
  /**
   *
   */
  public String getRegex() {
    return re;
  }
  
  /**
   *
   */
  public String getMatchType() {
    return matchType;
  }

  /**
   * Returns counter field name. This is used in multiple SQL queries.
   *
   * Example: CNTKEY1
   */
  public String getCntFName() {
    return "CNTKEY"+getId();
  }

  /**
   * Returns auxiliary counter field name. This is used in multiple SQL queries.
   *
   * Example: AUXCNTKEY1
   */
  public String getAuxCntFName() {
    return "AUXCNTKEY"+getId();
  }
}
