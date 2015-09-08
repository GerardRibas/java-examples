/**
 * 
 */
package cat.grc.spring.data.exception;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class CustomerWithAccountsException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public CustomerWithAccountsException(String message) {
    super(message);
  }

}
