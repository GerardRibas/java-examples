/**
 * 
 */
package cat.grc.spring.data.exception;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class TransactionTypeHasTransactionsException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public TransactionTypeHasTransactionsException(String message) {
    super(message);
  }

}
