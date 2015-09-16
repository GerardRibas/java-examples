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

import cat.grc.spring.data.dto.TransactionTypeDto;
import cat.grc.spring.data.service.FinancialTransactionService;

@RestController
@RequestMapping("/types")
public class TransactionTypeController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionTypeController.class);

  private FinancialTransactionService service;

  @RequestMapping
  public Collection<TransactionTypeDto> getTransactionTypes(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "15") int size) {
    LOGGER.debug("Finding transaction types in page={}, size={}");
    return service.findAllTransactionTypes(page, size);
  }

  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public TransactionTypeDto createTransactionType(@RequestBody TransactionTypeDto type) {
    LOGGER.debug("Creating transaction type={}", type);
    return service.addTransactionType(type);
  }

  @RequestMapping("/{id}")
  public TransactionTypeDto findTransactionType(@PathVariable Long id) {
    LOGGER.debug("Finding transaction type by id={}", id);
    return service.findTransactionTypeById(id);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public TransactionTypeDto updateTransactionType(@PathVariable Long id, @RequestBody TransactionTypeDto type) {
    LOGGER.debug("Updating transaction type {}, id={}", type, id);
    return service.updateTransactionType(type);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTransactionType(@PathVariable Long id) {
    LOGGER.debug("Deleting transaction type by id={}", id);
    service.deleteTransactionType(id);
  }

  @Resource
  public void setService(FinancialTransactionService service) {
    this.service = service;
  }

}
