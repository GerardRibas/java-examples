package cat.grc.spring.data.exception;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class ResourceAlreadyExistsException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ResourceAlreadyExistsException(String message) {
    super(message);
  }

}
