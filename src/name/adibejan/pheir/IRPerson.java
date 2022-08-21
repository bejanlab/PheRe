package name.adibejan.pheir;

import name.adibejan.util.IntCounterHashtable;
import name.adibejan.util.ExceptionUtil;

import java.util.*;
import java.sql.*;

import static java.lang.System.out;

/**
 * Patient structure enclosing all the documents of a patient.
 *
 * @author Cosmin Adrian Bejan
 * @version 1.0
 * @since JDK1.8 | Nov 2015
 */
public class IRPerson implements java.io.Serializable {
  static final long serialVersionUID = 198477808L;
  
  private int pid;
  private List<IRNote> docs;
  private IntCounterHashtable keyCounts; // key counts
  //private TIntDoubleHashMap keyWeights; // keyid -- weight
  private double relevanceScore;
  private boolean isAnnotated;
  private String annotation;
  private int matchingSize; //number of distinct keys matched by this patient 
  
  //private static PreparedStatement psPatientNotes01 = null;  //renamed: psGetPDocs

  /** 
   *
   */
  public IRPerson(int pid,
                  IntCounterHashtable keyCounts,
                  //TIntDoubleHashMap keyWeights,
                  double relevanceScore,
                  int matchingSize) {
    this.pid = pid;
    this.keyCounts = keyCounts;
    //this.keyWeights = keyWeights;
    this.relevanceScore = relevanceScore;
    this.matchingSize = matchingSize;
    isAnnotated = false;
  }
  
  /**
   *
   */
  public int getPID() {
    return pid;
  }
  
  /**
   *
   */
  public String getSPID() {
    return ""+pid;
  }
  
  public double getScore() {
    return relevanceScore;
  }

  
  public boolean isAnnotated() {
    return isAnnotated;
  }
  
  public void setAnnotation(String annotation) {
    this.annotation = annotation;
    isAnnotated = true;
  }

  public String getAnnotation() {
    return annotation;
  }

  /**
   * Returns the list of patient entries ranked by their relevance
   */
  public static List<IRPerson> getRankedPatientList(Hashtable<Integer, IRPerson> patients) {
    List<IRPerson> list = new ArrayList<IRPerson>(patients.values());
    Collections.sort(list, getDescComparatorByRelevance());
    return list;
  }
  
  /**
   * Comparator for ranking patient entries by their relevance + key
   */
  public static Comparator<IRPerson> getDescComparatorByRelevance() {
    return new Comparator<IRPerson>() {
      public int compare(IRPerson e1, IRPerson e2) {
        int cmpByScore = Double.compare(e2.relevanceScore, e1.relevanceScore);
        if(cmpByScore != 0)
          return cmpByScore;
        else
          return Integer.compare(e2.pid, e1.pid);
      }
    };
  }
}
