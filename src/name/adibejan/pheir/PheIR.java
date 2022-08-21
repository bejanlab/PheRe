package name.adibejan.pheir;

import name.adibejan.util.ExceptionUtil;
import name.adibejan.util.IntCounterHashtable;
import name.adibejan.util.Stopwatch;
import name.adibejan.util.JavaTimeUtil;
import name.adibejan.util.DBManager;
import name.adibejan.util.Counter01;
import name.adibejan.string.RE;
import name.adibejan.io.TextFile;

import java.util.*;
import java.sql.*;
import java.io.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import gnu.trove.map.hash.TIntObjectHashMap;

import static java.lang.System.out;

/**
 * PheIR - Phenotype Information Retrieval
 *
 * @author Cosmin Adrian Bejan
 * @version 1.0
 * @since JDK1.8 | March 2019
 */
public class PheIR {
  private static int CHAR_WINDOW_SIZE = 60;
  
  private TIntObjectHashMap<RE> keys;
  
  //------------------------------------------------------- counts
  private long Ntok;  // total no of words
  private long Ndoc;  // total no of docs
  private long Npatient; //total no of patinetns with clinical docs
  private long Nanykeypatient; //total no of patinetns with any key
  
  private long[] countKey;     // key -- global counts for the key expression
  private long[] countDoc;     // key -- global counts of docs with the key expression
  private long[] countPatient; // key -- global counts of patients with key expression  
  //IPF = inverse patient frequency
  private double[] IPF;   //key -- Math.log(Npatient) - Math.log(countPatient.get(index))
  
  //-------------------------------------------------------- maps
  private Hashtable<Integer, IRPerson> patients = null;
  private List<IRPerson> rankedPatients = null;  
  
  //-------------------------------------------------------- utils
  private static Stopwatch watch = null;


  static {
    watch = Stopwatch.getInstance();
  }
  
  /**
   * Loads the keys from a specified file
   *
   */ 
  public PheIR(String reFName) {
    keys = new TIntObjectHashMap<RE>();
    
    RE re = null;    
    int index = 0;
    for(String line : TextFile.read2listTrimAllLines(reFName)) {
      re = RE.getInstance(index, line, "|");
      keys.put(re.getId(), re);
      index++;
    }
  }

  /**
   *
   */
  public int getRankedPatientsSize() {
    return rankedPatients.size();
  }

  /**
   *
   */
  public IRPerson getRankedPatient(int i) {
    return rankedPatients.get(i);
  }

  /**
   *
   */
  public static void setContextSize(int size) {
    CHAR_WINDOW_SIZE = size;
  }
  
  /**
   *
   */
  public static int getContextSize() {
    return CHAR_WINDOW_SIZE;
  }
  
  //---------------------------------------------------------------------------- data extraction
  
  /**
   * exracts a ranked list of patients for the specified list of REs
   */
  public void loadDataStructures() {
    
    //[ 1 ] compute the key counts
    printRE();
    extractRECounts();
    printRECounts();
    
    //[ 2 ] compute key counts for each patient (with at least one key)
    out.print("Extracting relevant patients ...");
    watch.restart();
    patients = new Hashtable<Integer, IRPerson>();
    selectRelevantPatients(patients);
    long duration = watch.getDuration();
    out.println(" runtimme: ["+JavaTimeUtil.getDurationFormat_HH_MM_SS(duration)+"]");
    
    // [ 3 ] rank patients
    rankedPatients = IRPerson.getRankedPatientList(patients);
    patients.clear();
    patients = null;
  }

  /**
   * 
   */
  private void markAnnotations(Hashtable<Integer, String> annoMap) {
    IRPerson patient = null;
    for(int i=0; i < rankedPatients.size(); i++) {
      patient = rankedPatients.get(i);
      if(annoMap.containsKey(patient.getPID()))
        patient.setAnnotation(annoMap.get(patient.getPID()));        
    }    
  }
  
  /**
   * Extracts the key counts
   *
   */
  public void extractRECounts() {
    String cntFName = "CNT";
    long duration = 0;
    
    // [ 1 ] total counts
    out.print("Extracting total RE counts ...");
    watch.restart();
    Ntok = Counter01.countQueryCol(qSQLTotalWords(cntFName), cntFName);
    out.println("Ntok="+Ntok);
    
    Ndoc = Counter01.countQueryCol(qSQLTotalCDocs(cntFName), cntFName);
    out.println("Ndoc="+Ndoc);

    Npatient = Counter01.countQueryCol(qSQLTotalPatients(cntFName), cntFName);
    out.println("Npatient="+Npatient);
    
    Nanykeypatient = Counter01.countQueryCol(qSQLTotalAnyKeyPatients(cntFName), cntFName);
    out.println("Nanykeypatient="+Nanykeypatient);

    
    duration = watch.getDuration();
    out.println(" runtimme: ["+JavaTimeUtil.getDurationFormat_HH_MM_SS(duration)+"]");
    
    // [ 2 ] global key counts
    out.print("Extracting global key counts ...");
    watch.restart();    
    countKey = new long[keys.size()];
    assignSQLCountValues(qSQLGlobalKeyCounts(), countKey);
    duration = watch.getDuration();
    out.println(" runtimme: ["+JavaTimeUtil.getDurationFormat_HH_MM_SS(duration)+"]");
    
    // [ 3 ] global doc counts
    out.print("Extracting global doc counts ...");
    watch.restart();
    countDoc = new long[keys.size()];
    assignSQLCountValues(qSQLGlobalDocCounts(), countDoc);
    duration = watch.getDuration();
    out.println(" runtimme: ["+JavaTimeUtil.getDurationFormat_HH_MM_SS(duration)+"]");
    
    // [ 4 ] global patient counts
    out.print("Extracting global patient counts ...");
    watch.restart();
    countPatient = new long[keys.size()];
    assignSQLCountValues(qSQLGlobalPatientCounts(), countPatient);
    duration = watch.getDuration();
    out.println(" runtimme: ["+JavaTimeUtil.getDurationFormat_HH_MM_SS(duration)+"]");

    // [ 5 ] compute IPF = Math.log(Npatient) - Math.log(countPatient.get(index))
    IPF = new double[keys.size()];
    double logNpatient = Math.log(Npatient);
    for(int index = 0; index < keys.size(); index++)
      if(countPatient[index] != 0)
        IPF[index] = logNpatient - Math.log(countPatient[index]);
      else
        IPF[index] = logNpatient;
  }

  /**
   * Extract all patients with at least one key. For each patient, computes its 
   * relevance score.
   *
   */
  private void selectRelevantPatients(Hashtable<Integer, IRPerson> patients) {          
    Statement stmt = null;
    ResultSet rs = null;
    IRPerson patient = null;
    int cnt = 1;

    Connection conn = DBManager.getDBConnection();
    try {
      stmt = conn.createStatement();      
      rs = stmt.executeQuery(qSQLPatientCounts());
      while (rs.next()) {
        patient = loadRelevantPatient(rs);
        patients.put(patient.getPID(), patient);
        if(cnt % 10000 == 0) out.print(" "+cnt);
        cnt++;
      }
    } catch(SQLException sqle) { ExceptionUtil.trace(sqle, "sql selectRelevantPatients");
    } finally {
      try {
        if(rs != null) rs.close();
        if(stmt != null) stmt.close();
      } catch(SQLException sqle) { ExceptionUtil.trace(sqle, "close selectRelevantPatients"); }
    }
  }
  
  /**
   * Constructs the data structure for a patient:
   * - runs SQL for computing patient key conts
   * - computes key weights
   * - compute matching size
   * - computes patient relevance score
   *
   * Note: the patient key counts are represented using int values
   */
  private IRPerson loadRelevantPatient(ResultSet rs) throws SQLException {
    IntCounterHashtable keyCounts = new IntCounterHashtable();
    int matchingSize = 0;
    int keyCount = 0;
    double keyWeight = 0;
    double relevanceScore = 0;

    for(int index = 0; index < keys.size(); index++) {
      keyCount = rs.getInt(keys.get(index).getCntFName());
      keyCounts.put(index, keyCount);
      
      //keyWeight = keyCount * (Math.log(Npatient) - Math.log(countPatient.get(index)));
      // optimezed version: logDifference is stored in IPF map now
      keyWeight = keyCount * IPF[index];

      relevanceScore += keyWeight;
      
      if(keyCount > 0)
        matchingSize++;
    }
    
    return new IRPerson(rs.getInt("PERSON_ID"), keyCounts, relevanceScore, matchingSize);
  }
  
  //----------------------------------------------------------------------------- SQL queries
  /**
   * Counts total number of words in SD
   *
   */
  public static String qSQLTotalWords(String cntFieldName) {
    out.println("[qSQLTotalWords][select SUM(regexp_match_count(NOTE_TEXT, '\\b[A-Za-z0-9_]+\\b', 'i')) "+cntFieldName+" from NOTE]");
    
    return "select SUM(regexp_match_count(NOTE_TEXT, '\\b[A-Za-z0-9_]+\\b', 'i')) "+cntFieldName+" from NOTE";
  }
  
  /**
   * Counts total number of clin docs in SD
   *
   */
  public static String qSQLTotalCDocs(String cntFieldName) {
    out.println("[qSQLTotalCDocs][select count(distinct NOTE_TEXT) " + cntFieldName + " from NOTE]");
    
    return "select count(distinct NOTE_TEXT) " + cntFieldName + " from NOTE";
  }
  
  /**
   * Counts total number of patients in SD
   *
   */
  public static String qSQLTotalPatients(String cntFieldName) {
    out.println("[qSQLTotalPatients][select count(distinct PERSON_ID) " + cntFieldName + " from NOTE]");
    
    return "select count(distinct PERSON_ID) " + cntFieldName + " from NOTE";
  }
  
  /**
   * Counts the total number of patients with at least one RE (any RE)
   *
   */
  public String qSQLTotalAnyKeyPatients(String cntFieldName) {
    RE re = null;
    StringBuilder builder = new StringBuilder();
    
    re = keys.get(0);
    builder.append("select COUNT(distinct PERSON_ID) ");
    builder.append(cntFieldName);
    builder.append(" from NOTE where regexp_like(NOTE_TEXT, ");
    builder.append("'"+re.getRegex()+"'");
    builder.append(", '"+re.getMatchType()+"')");
    for(int index = 1; index < keys.size(); index++) {
      re = keys.get(index);
      builder.append(" or regexp_like(NOTE_TEXT, ");
      builder.append("'"+re.getRegex()+"'");
      builder.append(", '"+re.getMatchType()+"')");
    }

    out.println("[qSQLTotalAnyKeyPatients]["+builder.toString()+"]");
    
    return builder.toString();
  }

  /**
   * Query for global RE counts
   *
   */
  public String qSQLGlobalKeyCounts() {
    RE re = null;
    StringBuilder builder = new StringBuilder();
    
    builder.append("select ");
    re = keys.get(0);
    
    builder.append("SUM(regexp_match_count(NOTE_TEXT, ");
    builder.append("'"+re.getRegex()+"'");
    builder.append(", '"+re.getMatchType()+"')) AS ");
    builder.append(re.getCntFName());
    for(int index = 1; index < keys.size(); index++) {
      re = keys.get(index);
      builder.append(", SUM(regexp_match_count(NOTE_TEXT, ");
      builder.append("'"+re.getRegex()+"'");
      builder.append(", '"+re.getMatchType()+"')) AS ");
      builder.append(re.getCntFName());
    }
    builder.append(" from NOTE");

    out.println("[qSQLGlobalKeyCounts]["+builder.toString()+"]");

    return builder.toString();
  }

  /**
   * Query for global doc counts (docs with a specific key)
   *
   */
  public String qSQLGlobalDocCounts() {
    RE re = null;
    StringBuilder builder = new StringBuilder();
    
    builder.append("select ");
    re = keys.get(0);
      
    builder.append("SUM(CASE WHEN regexp_match_count(NOTE_TEXT, ");
    builder.append("'"+re.getRegex()+"'");
    builder.append(", '"+re.getMatchType()+"') > 0 THEN 1 ELSE 0 END) AS ");
    builder.append(re.getCntFName());
    for(int index = 1; index < keys.size(); index++) {
      re = keys.get(index);
      builder.append(", SUM(CASE WHEN regexp_match_count(NOTE_TEXT, ");
      builder.append("'"+re.getRegex()+"'");
      builder.append(", '"+re.getMatchType()+"') > 0 THEN 1 ELSE 0 END) AS ");
      builder.append(re.getCntFName());
    }
    builder.append(" from NOTE");

    out.println("[qSQLGlobalDocCounts]["+builder.toString()+"]");

    return builder.toString();
  }
  
  /**
   * Query for global patient counts (patients with documents with a specific key)
   *
   */
  public String qSQLGlobalPatientCounts() {
    RE re = null;
    StringBuilder builder = new StringBuilder();
    
    builder.append("select ");
    re = keys.get(0);
    
    builder.append("SUM(CASE WHEN A.");
    builder.append(re.getAuxCntFName());
    builder.append(" > 0 THEN 1 ELSE 0 END) AS ");
    builder.append(re.getCntFName());
    for(int index = 1; index < keys.size(); index++) {
      re = keys.get(index);
      builder.append(", SUM(CASE WHEN A.");
      builder.append(re.getAuxCntFName());
      builder.append(" > 0 THEN 1 ELSE 0 END) AS ");
      builder.append(re.getCntFName());
    }
    
    builder.append(" from (select ");
    re = keys.get(0);
    
    builder.append("SUM(regexp_match_count(NOTE_TEXT, ");
    builder.append("'"+re.getRegex()+"'");
    builder.append(", '"+re.getMatchType()+"')) AS ");
    builder.append(re.getAuxCntFName());
    for(int index = 1; index < keys.size(); index++) {
      re = keys.get(index);
      builder.append(", SUM(regexp_match_count(NOTE_TEXT, ");
      builder.append("'"+re.getRegex()+"'");
      builder.append(", '"+re.getMatchType()+"')) AS ");
      builder.append(re.getAuxCntFName());      
    }   
    builder.append(" from NOTE group by PERSON_ID) A");

    out.println("[qSQLGlobalPatientCounts]["+builder.toString()+"]");

    return builder.toString();
  }  
  
  /**
   * Query for extracting key counts for each patient. Only patients with at least one 
   * key in their docs are selected
   *
   */
  public String qSQLPatientCounts() {
    RE re = null;
    StringBuilder builder = new StringBuilder();
    
    builder.append("select PERSON_ID, ");
    re = keys.get(0);
    
    builder.append("SUM(regexp_match_count(NOTE_TEXT, ");
    builder.append("'"+re.getRegex()+"'");
    builder.append(", '"+re.getMatchType()+"')) AS ");
    builder.append(re.getCntFName());
    for(int index = 1; index < keys.size(); index++) {
      re = keys.get(index);
      builder.append(", SUM(regexp_match_count(NOTE_TEXT, ");
      builder.append("'"+re.getRegex()+"'");
      builder.append(", '"+re.getMatchType()+"')) AS ");
      builder.append(re.getCntFName());
    }
    builder.append(" from NOTE group by PERSON_ID having ");
    re = keys.get(0);
    
    builder.append(re.getCntFName());
    builder.append(" > 0");
    for(int index = 1; index < keys.size(); index++) {
      re = keys.get(index);
      builder.append(" or ");
      builder.append(re.getCntFName());
      builder.append(" > 0");
    }

    out.println("[qSQLPatientCounts]["+builder.toString()+"]");

    return builder.toString();
  }

  /**
   * All the notes with at least one RE (any RE)
   *
   */
  public String qSQLAnyKeyNotes() {
    RE re = null;
    StringBuilder builder = new StringBuilder();
    
    re = keys.get(0);
    builder.append("select distinct PERSON_ID, NOTE_ID, NOTE_DATE, NOTE_DATETIME, NOTE_TEXT ");
    builder.append(" from NOTE where regexp_like(NOTE_TEXT, ");
    builder.append("'"+re.getRegex()+"'");
    builder.append(", '"+re.getMatchType()+"')");
    for(int index = 1; index < keys.size(); index++) {
      re = keys.get(index);
      builder.append(" or regexp_like(NOTE_TEXT, ");
      builder.append("'"+re.getRegex()+"'");
      builder.append(", '"+re.getMatchType()+"') ");
    }
    builder.append("order by PERSON_ID, NOTE_DATETIME asc");
    
    out.println("[qSQLAnyKeyNotes]["+builder.toString()+"]");
    
    return builder.toString();
  }

  
  //----------------------------------------------------------------------------- utils
  
  /**
   * For each RE, it extracts the corresponding count after running the SQL query
   *
   * Used to extract: 
   * - global key counts, 
   * - global doc counts, 
   * - global patient counts
   *
   * Note: the global key counts are represented using long values
   */
  private void assignSQLCountValues(String query, long[] map) {
    Statement stmt = null;
    ResultSet rs = null;     
    Connection conn = DBManager.getDBConnection();
    try {
      stmt = conn.createStatement();      
      rs = stmt.executeQuery(query);
      if(rs.next()) 
        for(int index = 0; index < keys.size(); index++)          
          map[index] = rs.getLong(keys.get(index).getCntFName());
    } catch(SQLException sqle) {
      out.println("Exception in executing the sql statement: "+sqle.getMessage());
      out.println("DB query: " + query);
      sqle.printStackTrace();
    } finally {
      try {
        if(rs != null) rs.close();
        if(stmt != null) stmt.close();
      } catch(SQLException sqle) {
        out.println("Exception in executing the sql statement: "+sqle.getMessage());
        out.println("DB query: " + query);
        sqle.printStackTrace();
      }
    }
  }

  //------------------------------------------------------------------------------ print
  
  /**
   * Print keys in ascending order by keyId
   */
  public void printRE() {
    for(int index=0; index < keys.size(); index++)
      keys.get(index).print01();
    //out.println(""+index+": "+keys.get(index).toString(",", "'"));
  }
  
  /**
   * Print keys in keyId ascending order
   *
   */
  public void printRECounts() {
    out.println("Ntok: " + Ntok);
    out.println("Ndoc: " + Ndoc);
    out.println("Npatient: " + Npatient);
    out.println("Npatientswithkeys: " + Nanykeypatient);
    
    out.printf("\n%3s %30s %15s %15s %15s\n", "ID", "KEY", "KEY CNT", "DOC CNT", "PAT CNT");
    for(int index=0; index < keys.size(); index++)
      out.printf("%3d: %30s %15d %15d %15d\n", index, keys.get(index).getRegex(), countKey[index], countDoc[index], countPatient[index]);
  }

  /**
   *
   */
  public static void testPhenotypeRetrieval() {
    PheIR ir = new PheIR("PATH to the file with phenotype query terms");
    ir.loadDataStructures();
    
    IRPerson patient = null;
    for(int i = 0; i < ir.getRankedPatientsSize(); i++) {
      patient = ir.getRankedPatient(i);
      out.println("PID:"+patient.getPID()+" Score:"+patient.getScore());
    }
  }
  
  /**
   *
   */
  public static void main(String[] args) {
    testPhenotypeRetrieval();
  }
}
