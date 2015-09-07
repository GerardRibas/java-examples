/**
 * 
 */
package cat.grc.spring.data.exception;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class InvoiceWithTransactionsException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public InvoiceWithTransactionsException(String message) {
    super(message);
  }

}
