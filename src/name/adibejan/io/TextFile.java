package name.adibejan.io;

import name.adibejan.util.ExceptionUtil;                          

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedWriter;

import java.util.*;

import static java.lang.System.out;

/**
 * Tool for easy acees to a text file
 *
 * @author Cosmin Adrian Bejan
 * @version 1.0
 * @since JDK1.6
 */
public class TextFile {
  /**
   * Restricts access to instances of this class
   */
  private TextFile() {}
  
  /**
   *
   */
  static public List<String> read2listTrimAllLines(String fileName) {
    return readIntoList(new File(fileName), "UTF-8", true, false);
  }

  /**
   * Fetches the entire contents of a text file, and return it in a list of strings.
   *
   * @param file the file to read from
   * @param charsetName the name of a supported charset
   * @return the entire content of the file
   */
  static public List<String> readIntoList(File file,
                                          String charsetName,
                                          boolean TRIM_FLAG,
                                          boolean SKIP_HEADER) {
    List<String> list = new ArrayList<String>();    
    BufferedReader input = null;
    String line = null;

    try {
      input = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));

      if(SKIP_HEADER) line = input.readLine();
      while (( line = input.readLine()) != null) {
        if(TRIM_FLAG)
          list.add(line.trim());
        else
          list.add(line);
      }
    }
    catch (FileNotFoundException ex) { ExceptionUtil.trace(ex); }
    catch (IOException ioe){ ExceptionUtil.trace(ioe, "Error in reading the file:"); }
    finally {
      try {
        if (input!= null) input.close();
      } catch (IOException ioe) { ExceptionUtil.trace(ioe, "Error in closing the file:"); }
    }
    return list;
  }
} 

