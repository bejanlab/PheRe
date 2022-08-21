package name.adibejan.util;

import name.adibejan.util.ExceptionUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import java.io.IOException;

import static java.lang.System.out;

/**
 * SQL util
 *
 * @author Cosmin Adrian Bejan
 * @version 1.0
 * @since JDK1.8 | Dec 2016
 */
public class Counter01 {
  
  /**
   * Returns the value of a count SQL query.
   *
   * @param fieldName indicates the count value
   */
  public static long countQueryCol(String query, String fieldName) {
    return countQueryCol(query, fieldName, DBManager.getDBConnection());
  }
  
  /**
   * Returns the value of a count SQL query.
   *
   * @param fieldName indicates the count value
   */
  public static long countQueryCol(String query, String fieldName, Connection conn) {
    Statement stmt = null;
    ResultSet rs = null;    
    
    try {
      stmt = conn.createStatement();      
      rs = stmt.executeQuery(query);
  
      if(rs.next())
        return rs.getLong(fieldName);
      
    } catch(SQLException sqle) { ExceptionUtil.trace(sqle, "SQL query exec ["+query+"]");
    } finally {
      try {
        if(rs != null) rs.close();
        if(stmt != null) stmt.close();
      } catch(SQLException sqle) { ExceptionUtil.trace(sqle, "SQL close ["+query+"]"); }
    }
    throw new RuntimeException("Could not retrive the count for this query:["+query+"]");
  }
}
