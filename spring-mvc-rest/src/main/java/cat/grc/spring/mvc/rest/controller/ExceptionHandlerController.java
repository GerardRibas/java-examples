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

import cat.grc.spring.data.exception.AccountWithTransactionsException;
import cat.grc.spring.data.exception.CustomerWithAccountsException;
import cat.grc.spring.data.exception.CustomerWithOrdersException;
import cat.grc.spring.data.exception.OrderWithInvoicesException;
import cat.grc.spring.data.exception.ProductCategoryHasProductsException;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.exception.TransactionTypeHasTransactionsException;
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

  @ExceptionHandler(ProductCategoryHasProductsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  public ErrorResource handleProductCategoryHasProductsException(Exception ex) throws IOException {
    return new ErrorResource(Instant.now().getEpochSecond(), "E003", ex.getMessage());
  }

  @ExceptionHandler(CustomerWithOrdersException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  public ErrorResource handleCustomerWithOrdersException(Exception ex) throws IOException {
    return new ErrorResource(Instant.now().getEpochSecond(), "E004", ex.getMessage());
  }

  @ExceptionHandler(CustomerWithAccountsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  public ErrorResource handleCustomerWithAccountsException(Exception ex) throws IOException {
    return new ErrorResource(Instant.now().getEpochSecond(), "E005", ex.getMessage());
  }

  @ExceptionHandler(AccountWithTransactionsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  public ErrorResource handleAccountWithTransactionsException(Exception ex) throws IOException {
    return new ErrorResource(Instant.now().getEpochSecond(), "E006", ex.getMessage());
  }

  @ExceptionHandler(OrderWithInvoicesException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  public ErrorResource handleOrderWithInvoicesException(Exception ex) throws IOException {
    return new ErrorResource(Instant.now().getEpochSecond(), "E007", ex.getMessage());
  }

  @ExceptionHandler(TransactionTypeHasTransactionsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  public ErrorResource handleTransactionTypeHasTransactionsException(Exception ex) throws IOException {
    return new ErrorResource(Instant.now().getEpochSecond(), "E008", ex.getMessage());
  }

}
