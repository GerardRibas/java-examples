/**
 * 
 */
package cat.grc.spring.data.exception;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class ProductCategoryHasProductsException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ProductCategoryHasProductsException(String message) {
    super(message);
  }

}
