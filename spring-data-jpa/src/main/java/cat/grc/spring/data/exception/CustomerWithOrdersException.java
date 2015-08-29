/**
 * 
 */
package cat.grc.spring.data.exception;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class CustomerWithOrdersException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public CustomerWithOrdersException(String message) {
    super(message);
  }

}
