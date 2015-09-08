/**
 * 
 */
package cat.grc.spring.mvc.rest;

import java.io.Serializable;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class ErrorResource implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long timestamp;

  private String code;

  private String message;

  public ErrorResource() {
    // Default constructor
  }

  public ErrorResource(Long timestamp, String code, String message) {
    super();
    this.timestamp = timestamp;
    this.code = code;
    this.message = message;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}
