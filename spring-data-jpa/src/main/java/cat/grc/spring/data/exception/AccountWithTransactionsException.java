/**
 * 
 */
package cat.grc.spring.data.exception;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class AccountWithTransactionsException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public AccountWithTransactionsException(String message) {
    super(message);
  }

}
