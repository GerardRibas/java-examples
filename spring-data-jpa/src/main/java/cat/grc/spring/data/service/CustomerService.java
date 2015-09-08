package cat.grc.spring.data.service;

import java.util.Collection;

import cat.grc.spring.data.dto.AccountDto;
import cat.grc.spring.data.dto.CustomerDto;
import cat.grc.spring.data.exception.AccountWithTransactionsException;
import cat.grc.spring.data.exception.CustomerWithAccountsException;
import cat.grc.spring.data.exception.CustomerWithOrdersException;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;


/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface CustomerService {

  /**
   * Find all customers
   * 
   * @param page the page requested
   * @param size the size of the page
   * @return a customers collection
   */
  Collection<CustomerDto> findAllCustomers(int page, int size);

  /**
   * Find customer by the id
   * 
   * @param id the id of the customer
   * @return the customer
   * @throws ResourceNotFoundException if the customer can't be found
   */
  CustomerDto findCustomerById(Long id);

  /**
   * Create a new customer.
   * 
   * @param the new customer
   * @return the customer saved into the database
   * @throws ResourceAlreadyExistsException if the customer was already saved previously
   */
  CustomerDto addCustomer(CustomerDto customer);

  /**
   * Update a customer
   * 
   * @param the customer to update
   * @return the customer updated
   * @throws ResourceNotFoundException if the customer is not saved previously
   */
  CustomerDto updateCustomer(CustomerDto customer);

  /**
   * Delete a customer.
   * 
   * @param id of the customer to be deleted
   * @throws CustomerWithAccountsException if the customer has accounts
   * @throws CustomerWithOrdersException if the customer has orders created
   * @throws ResourceNotFoundException if the customer can't be found
   */
  void deleteCustomer(Long id);

  /**
   * Find accounts of the customerId
   * 
   * @param customerId the customer id
   * @param page the page requested
   * @param size the size of the page
   * @return the accounts collection
   */
  Collection<AccountDto> findAllAccountsByCustomerId(Long customerId, int page, int size);

  /**
   * Find an account
   * 
   * @param id of the account
   * @return the Account that match with the given id
   * @throws ResourceNotFoundException if the Account can't be found
   */
  AccountDto findAccountById(Long id);

  /**
   * Add an account
   * 
   * @param account the account to add
   * @return the account saved
   * @throws ResourceAlreadyExistsException if the account was previously
   * @throws ResourceNotFoundException if the customer can't be found
   */
  AccountDto addAccount(AccountDto account);

  /**
   * Update an account
   * 
   * @param account to be updated
   * @return the account updated
   * @throws ResourceNotFoundException if the account can't be found or the customer doesn't exists.
   */
  AccountDto updateAccount(AccountDto account);

  /**
   * Delete an account
   * 
   * @param id of the account to be deleted
   * @throws ResourceNotFoundException if the account id doesn't exists
   * @throws AccountWithTransactionsException if the account has transactions
   */
  void deleteAccount(Long id);

}
