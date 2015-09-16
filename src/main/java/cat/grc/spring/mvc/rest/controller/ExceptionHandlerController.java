/**
 * 
 */
package cat.grc.spring.mvc.rest.controller;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.mvc.rest.ErrorResource;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@ControllerAdvice
public class ExceptionHandlerController {

  @ExceptionHandler(ResourceAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  public ErrorResource handleResourceAlreadyExistsException(Exception ex) throws IOException {
    return new ErrorResource(Instant.now().getEpochSecond(), "E001", ex.getMessage());
  }
  
  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorResource handleResourceNotFoundException(Exception ex) throws IOException {
    return new ErrorResource(Instant.now().getEpochSecond(), "E002", ex.getMessage());
  }

}
