/**
 * 
 */
package cat.grc.spring.data.exception;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class OrderWithInvoicesException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public OrderWithInvoicesException(String message) {
    super(message);
  }

}
