/**
 * 
 */
package cat.grc.spring.data.service;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import cat.grc.spring.data.dto.AccountDto;
import cat.grc.spring.data.dto.CustomerDto;
import cat.grc.spring.data.entity.Account;
import cat.grc.spring.data.entity.Customer;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.repository.AccountRepository;
import cat.grc.spring.data.repository.CustomerRepository;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class CustomerServiceImpl implements CustomerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

  private CustomerRepository customerRepository;

  private AccountRepository accountRepository;

  private ModelMapper modelMapper;

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.CustomerService#findAllCustomers(int, int)
   */
  @Override
  @Transactional(readOnly = true)
  public Collection<CustomerDto> findAllCustomers(int page, int size) {
    LOGGER.debug("Finding customers by page={} and size={}", page, size);
    Pageable pageable = new PageRequest(page, size);
    Page<Customer> customerPage = customerRepository.findAll(pageable);
    return customerPage.getContent().stream().map(customer -> modelMapper.map(customer, CustomerDto.class))
        .collect(Collectors.toList());
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.CustomerService#findCustomerById(java.lang.Long)
   */
  @Override
  @Transactional(readOnly = true)
  public CustomerDto findCustomerById(Long id) {
    LOGGER.debug("Finding category by id={}", id);
    Assert.notNull(id);
    Customer entity = customerRepository.findOne(id);
    if (entity == null) {
      String msg = String.format("No Customer found for id=%d", id);
      LOGGER.warn(msg);
      throw new ResourceNotFoundException(msg);
    }
    return modelMapper.map(entity, CustomerDto.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cat.grc.spring.data.service.CustomerService#addCustomer(cat.grc.spring.data.dto.CustomerDto)
   */
  @Override
  @Transactional
  public CustomerDto addCustomer(CustomerDto customer) {
    LOGGER.debug("Adding new customer {}", customer);
    Assert.notNull(customer);
    boolean exists = customer.getId() == null ? false : customerRepository.exists(customer.getId());
    if (exists) {
      String msg = String.format("CustomerId=%s already exists", customer.getId());
      LOGGER.warn(msg);
      throw new ResourceAlreadyExistsException(msg);
    }
    return modelMapper.map(customerRepository.save(modelMapper.map(customer, Customer.class)), CustomerDto.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cat.grc.spring.data.service.CustomerService#updateCustomer(cat.grc.spring.data.dto.CustomerDto)
   */
  @Override
  @Transactional
  public CustomerDto updateCustomer(CustomerDto customer) {
    LOGGER.debug("Updating customer {}", customer);
    Assert.notNull(customer);
    customerMustExists(customer.getId());
    return modelMapper.map(customerRepository.save(modelMapper.map(customer, Customer.class)), CustomerDto.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.CustomerService#deleteCustomer(java.lang.Long)
   */
  @Override
  @Transactional
  public void deleteCustomer(Long id) {
    LOGGER.debug("Delete customer by id={}", id);
    Assert.notNull(id);
    customerMustExists(id);
    customerRepository.delete(id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.CustomerService#findAllAccountsByCustomerId(java.lang.Long,
   * int, int)
   */
  @Override
  @Transactional(readOnly = true)
  public Collection<AccountDto> findAllAccountsByCustomerId(Long customerId, int page, int size) {
    LOGGER.debug("Finding accounts by userId={} and page={} and size={}", customerId, page, size);
    Pageable pageable = new PageRequest(page, size);
    Page<Account> accountsPage = accountRepository.findByCustomer(customerId, pageable);
    return accountsPage.getContent().stream().map(account -> modelMapper.map(account, AccountDto.class))
        .collect(Collectors.toList());
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.CustomerService#findAccountById(java.lang.Long)
   */
  @Override
  @Transactional(readOnly = true)
  public AccountDto findAccountById(Long id) {
    LOGGER.debug("Finding account by id={}", id);
    Assert.notNull(id);
    Account entity = accountRepository.findOne(id);
    if (entity == null) {
      String msg = String.format("No Account found for id=%d", id);
      LOGGER.warn(msg);
      throw new ResourceNotFoundException(msg);
    }
    return modelMapper.map(entity, AccountDto.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.CustomerService#addAccount(cat.grc.spring.data.dto.AccountDto)
   */
  @Override
  @Transactional
  public AccountDto addAccount(AccountDto account) {
    Assert.notNull(account);
    Assert.notNull(account.getCustomerId());
    boolean exists = account.getId() == null ? false : accountRepository.exists(account.getId());
    if (exists) {
      String msg = String.format("AccountId=%s already exists", account.getId());
      LOGGER.warn(msg);
      throw new ResourceAlreadyExistsException(msg);
    }
    return modelMapper.map(accountRepository.save(modelMapper.map(account, Account.class)), AccountDto.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cat.grc.spring.data.service.CustomerService#updateAccount(cat.grc.spring.data.dto.AccountDto)
   */
  @Override
  @Transactional
  public AccountDto updateAccount(AccountDto account) {
    LOGGER.debug("Updating account {}", account);
    Assert.notNull(account);
    accountMustExists(account.getId());
    return modelMapper.map(accountRepository.save(modelMapper.map(account, Account.class)), AccountDto.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.CustomerService#deleteAccount(java.lang.Long)
   */
  @Override
  @Transactional
  public void deleteAccount(Long id) {
    LOGGER.debug("Deleting account by id={}", id);
    accountMustExists(id);
    accountRepository.delete(id);
  }

  private boolean accountMustExists(Long id) {
    Assert.notNull(id);
    boolean exists = accountRepository.exists(id);
    if (!exists) {
      String msg = String.format("AccountId=%s does not exists", id);
      LOGGER.warn(msg);
      throw new ResourceNotFoundException(msg);
    }
    return exists;
  }

  private boolean customerMustExists(Long id) {
    Assert.notNull(id);
    boolean exists = customerRepository.exists(id);
    if (!exists) {
      String msg = String.format("CustomerId=%s does not exists", id);
      LOGGER.warn(msg);
      throw new ResourceNotFoundException(msg);
    }
    return exists;
  }

  @Resource
  public void setAccountRepository(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Resource
  public void setCustomerRepository(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  @Resource
  public void setModelMapper(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

}
