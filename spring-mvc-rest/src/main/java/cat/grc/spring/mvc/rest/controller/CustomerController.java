/**
 * 
 */
package cat.grc.spring.mvc.rest.controller;

import java.util.Collection;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import cat.grc.spring.data.dto.AccountDto;
import cat.grc.spring.data.dto.CustomerDto;
import cat.grc.spring.data.service.CustomerService;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);

  private CustomerService service;

  @RequestMapping
  public Collection<CustomerDto> getCustomers(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "15") int size) {
    LOGGER.debug("Finding all customers in page={} with size={}", page, size);
    return service.findAllCustomers(page, size);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerDto customer, UriComponentsBuilder builder) {
    LOGGER.debug("Creating Customer={}", customer);
    CustomerDto customerSaved = service.addCustomer(customer);
    UriComponents uriComponents = builder.path("customers/{id}").buildAndExpand(customer.getId());
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<CustomerDto>(customerSaved, headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/{id}")
  public CustomerDto findCustomer(@PathVariable Long id) {
    LOGGER.debug("Finding customer by id={}", id);
    return service.findCustomerById(id);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public CustomerDto updateCustomer(@PathVariable Long id, @RequestBody CustomerDto customer) {
    LOGGER.debug("Updating customer={} id={}", customer, id);
    return service.updateCustomer(customer);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteCustomer(@PathVariable Long id) {
    LOGGER.debug("Deleting customer by id={}", id);
    service.deleteCustomer(id);
  }

  @RequestMapping(value = "/{id}/accounts")
  public Collection<AccountDto> getAccountsOfACustomer(@PathVariable Long id,
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
    LOGGER.debug("Finding accounts for customerId={}", id);
    return service.findAllAccountsByCustomerId(id, page, size);
  }

  @RequestMapping(value = "/{id}/accounts", method = RequestMethod.POST)
  public ResponseEntity<AccountDto> createAccount(@PathVariable Long id, @RequestBody AccountDto account,
      UriComponentsBuilder builder) {
    LOGGER.debug("Creating Account={} for customer={}", account, id);
    AccountDto accountSaved = service.addAccount(account);
    UriComponents uriComponents = builder.path("customers/{customerId}/accounts/{id}")
        .buildAndExpand(accountSaved.getCustomerId(), accountSaved.getId());
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<AccountDto>(accountSaved, headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/{customerId}/accounts/{id}")
  public AccountDto findAccount(@PathVariable Long customerId, @PathVariable Long id) {
    LOGGER.debug("Finding account={} for customerId={}", id, customerId);
    return service.findAccountById(id);
  }

  @RequestMapping(value = "/{customerId}/accounts/{id}", method = RequestMethod.PUT)
  public AccountDto updateAccount(@PathVariable Long customerId, @RequestBody AccountDto account) {
    LOGGER.debug("Updating account={} for customerId={}", account, customerId);
    return service.updateAccount(account);
  }

  @RequestMapping(value = "/{customerId}/accounts/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAccount(@PathVariable Long customerId, @PathVariable Long id) {
    LOGGER.debug("Deleting accountId={} for customerId={}", id, customerId);
    service.deleteAccount(id);
  }

  @Resource
  public void setService(CustomerService service) {
    this.service = service;
  }

}
