package name.adibejan.pheir;

import static java.lang.System.out;

/**
 * Patient structure enclosing all the documents of a patient.
 *
 * @author Cosmin Adrian Bejan
 * @version 1.0
 * @since JDK1.8 | Nov 2015
 */
public class IRNote implements java.io.Serializable {
  static final long serialVersionUID = 1911808L;
  
  private long nid;
  private String strdt;
  private String content;
  
  /**
   *
   */
  public IRNote(long nid, String strdt, String content) {
    this.nid = nid;
    this.strdt = strdt;
    this.content = content;    
  }

  public String getContent() {
    return content;
  }

  public long getNID() {
    return nid;
  }
  
  public String getStrNoteDate() {
    return strdt;
  }
}
