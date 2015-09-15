package cat.grc.spring.mvc.rest.controller;

import java.util.Collection;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cat.grc.spring.data.dto.FinancialTransactionDto;
import cat.grc.spring.data.service.FinancialTransactionService;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RestController
public class TransactionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionTypeController.class);

  private FinancialTransactionService service;

  @RequestMapping(value = {"/invoices/{id}/transactions"})
  public Collection<FinancialTransactionDto> findTransactionsForInvoice(@PathVariable Long id,
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
    LOGGER.debug("Finding transactions for invoiceNumber={} page={}, size={}", id, page, size);
    return service.findTransactionsByInvoiceNumber(id, page, size);
  }

  @RequestMapping(value = {"/invoices/{id}/transactions", "/transactions"}, method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public FinancialTransactionDto createTransaction(@RequestBody FinancialTransactionDto transaction) {
    LOGGER.debug("Creating transaction={}", transaction);
    return service.payInvoice(transaction);
  }

  @RequestMapping(value = {"/transactions/{id}"})
  public FinancialTransactionDto findFinancialTransaction(@PathVariable Long id) {
    LOGGER.debug("Finding transaction by id={}", id);
    return service.findTransactionById(id);
  }

  @RequestMapping(value = "/transactions/{id}", method = RequestMethod.PUT)
  public FinancialTransactionDto updateFinancialTransaction(@PathVariable Long id,
      @RequestBody FinancialTransactionDto transaction) {
    LOGGER.debug("Updating financial transaction={}, id={}", transaction, id);
    return service.updateFinancialTransaction(transaction);
  }

  @RequestMapping(value = "/transactions/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteFinancialTransaction(@PathVariable Long id) {
    LOGGER.debug("Deleting transactions by id={}", id);
    service.deleteFinancialTransaction(id);
  }

  @Resource
  public void setService(FinancialTransactionService service) {
    this.service = service;
  }

}
