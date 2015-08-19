package cat.grc.spring.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import cat.grc.spring.data.Gender;
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
public class CustomerServiceTest {

  private CustomerServiceImpl service;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private AccountRepository accountRepository;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    service = new CustomerServiceImpl();
    service.setCustomerRepository(customerRepository);
    service.setAccountRepository(accountRepository);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindAllCustomers() {
    int page = 1;
    int size = 15;
    Pageable pageable = new PageRequest(page, size);
    Page<Customer> pageCustomers = mock(Page.class);
    Customer result = mock(Customer.class);
    when(customerRepository.findAll(eq(pageable))).thenReturn(pageCustomers);
    when(pageCustomers.getContent()).thenReturn(Arrays.asList(result));

    Collection<CustomerDto> categories = service.findAllCustomers(page, size);
    assertFalse("Expected a non empty collection", categories.isEmpty());
    assertEquals("Expected only one result", 1, categories.size());

    verify(customerRepository).findAll(eq(pageable));
    verifyNoMoreInteractions(customerRepository);
  }

  @Test
  public void testFindCustomerById() {
    Long customerId = 1L;
    Customer result = mock(Customer.class);
    when(customerRepository.findOne(eq(customerId))).thenReturn(result);
    CustomerDto customer = service.findCustomerById(customerId);
    assertNotNull("Expected a customer", customer);
    verify(customerRepository).findOne(eq(customerId));
    verifyNoMoreInteractions(customerRepository);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testFindCustomerById_ResourceNotFoundException() {
    Long customerId = 1L;
    when(customerRepository.findOne(eq(customerId))).thenReturn(null);
    service.findCustomerById(customerId);
  }

  @Test
  public void testAddCustomer() {
    CustomerDto dto = new CustomerDto(null, "Bart", "JoJo", "Simpson", Gender.MALE, "bart@simpson.com", "55512345",
        "742 Evergreen Terrace, Sprinfield");
    Customer expectedEntity = new Customer(dto.getId(), dto.getFirstName(), dto.getMiddleName(), dto.getLastName(),
        dto.getGender(), dto.getEmail(), dto.getPhoneNumber(), dto.getAddress());
    Customer entitySaved = new Customer(1L, dto.getFirstName(), dto.getMiddleName(), dto.getLastName(), dto.getGender(),
        dto.getEmail(), dto.getPhoneNumber(), dto.getAddress());
    CustomerDto expectedDtoSaved = new CustomerDto(entitySaved.getId(), dto.getFirstName(), dto.getMiddleName(),
        dto.getLastName(), dto.getGender(), dto.getEmail(), dto.getPhoneNumber(), dto.getAddress());
    when(customerRepository.save(eq(expectedEntity))).thenReturn(entitySaved);
    CustomerDto savedDto = service.addCustomer(dto);
    assertEquals(expectedDtoSaved, savedDto);
    verify(customerRepository).save(eq(expectedEntity));
    verify(customerRepository, new Times(0)).exists(anyLong());
    verifyNoMoreInteractions(customerRepository);
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testAddCustomer_ResourceAlreadyExistsException() {
    CustomerDto dto = new CustomerDto(1L, "Bart", "JoJo", "Simpson", Gender.MALE, "bart@simpson.com", "55512345",
        "742 Evergreen Terrace, Sprinfield");
    when(customerRepository.exists(eq(dto.getId()))).thenReturn(true);
    service.addCustomer(dto);
  }

  @Test
  public void testUpdateCustomer() {
    CustomerDto dto = new CustomerDto(1L, "Bart", "JoJo", "Simpson", Gender.MALE, "bart@simpson.com", "55512345",
        "742 Evergreen Terrace, Sprinfield");
    Customer expectedEntity = new Customer(dto.getId(), dto.getFirstName(), dto.getMiddleName(), dto.getLastName(),
        dto.getGender(), dto.getEmail(), dto.getPhoneNumber(), dto.getAddress());
    when(customerRepository.exists(eq(dto.getId()))).thenReturn(true);
    when(customerRepository.save(eq(expectedEntity))).thenReturn(expectedEntity);
    CustomerDto savedDto = service.updateCustomer(dto);
    assertEquals("Expected the same dto", dto, savedDto);
    verify(customerRepository).save(eq(expectedEntity));
    verify(customerRepository).exists(eq(dto.getId()));
    verifyNoMoreInteractions(customerRepository);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testUpdateCustomer_ResourceNotFoundException() {
    CustomerDto dto = new CustomerDto(1L, "Bart", "JoJo", "Simpson", Gender.MALE, "bart@simpson.com", "55512345",
        "742 Evergreen Terrace, Sprinfield");
    when(customerRepository.exists(eq(dto.getId()))).thenReturn(false);
    service.updateCustomer(dto);
  }

  @Test
  public void testDeleteCustomer() {
    Long customerId = 1L;
    when(customerRepository.exists(eq(customerId))).thenReturn(true);
    service.deleteCustomer(customerId);
    verify(customerRepository).delete(eq(customerId));
    verify(customerRepository).exists(eq(customerId));
    verifyNoMoreInteractions(customerRepository);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testDeleteCustomer_ResourceNotFoundException() {
    Long customerId = 1L;
    when(customerRepository.exists(eq(customerId))).thenReturn(false);
    service.deleteCustomer(customerId);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindAllAccountsByCustomerId() {
    int page = 1;
    int size = 15;
    Long customerId = 1L;
    Pageable pageable = new PageRequest(page, size);
    Page<Account> pageCustomers = mock(Page.class);
    Customer customer = mock(Customer.class);
    Account result = mock(Account.class);
    when(result.getCustomer()).thenReturn(customer);
    when(accountRepository.findByCustomer(eq(customerId), eq(pageable))).thenReturn(pageCustomers);
    when(pageCustomers.getContent()).thenReturn(Arrays.asList(result));

    Collection<AccountDto> accounts = service.findAllAccountsByCustomerId(customerId, page, size);
    assertFalse("Expected a non empty collection", accounts.isEmpty());
    assertEquals("Expected only one result", 1, accounts.size());

    verify(accountRepository).findByCustomer(eq(customerId), eq(pageable));
    verifyZeroInteractions(customerRepository);
  }

  @Test
  public void testFindAccountById() {
    Long accountId = 1L;
    Customer customer = mock(Customer.class);
    Account account = mock(Account.class);
    when(account.getCustomer()).thenReturn(customer);
    when(accountRepository.findOne(eq(accountId))).thenReturn(account);
    AccountDto accountDto = service.findAccountById(accountId);
    assertNotNull("Expected a dto", accountDto);
    verify(accountRepository).findOne(eq(accountId));
    verifyZeroInteractions(customerRepository);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testFindAccountById_ResourceNotFoundException() {
    Long accountId = 1L;
    when(accountRepository.findOne(eq(accountId))).thenReturn(null);
    service.findAccountById(accountId);
  }

  @Test
  public void testAddAccount() {
    AccountDto accountDto = new AccountDto(null, 1L, new Date(), "Some account name");
    Account account = new Account(accountDto.getId(), new Customer(accountDto.getCustomer()), accountDto.getOpened(),
        accountDto.getName());
    Account savedAccount =
        new Account(1L, new Customer(accountDto.getCustomer()), accountDto.getOpened(), accountDto.getName());
    AccountDto savedAccountDto = new AccountDto(savedAccount.getId(), savedAccount.getCustomer().getId(),
        savedAccount.getOpened(), savedAccount.getName());
    when(accountRepository.save(eq(account))).thenReturn(savedAccount);
    AccountDto savedDto = service.addAccount(accountDto);
    assertEquals("Dto are not equals", savedAccountDto, savedDto);
    verify(accountRepository).save(eq(account));
    verify(accountRepository, new Times(0)).exists(anyLong());
    verifyZeroInteractions(customerRepository);
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testAddAccount_ResourceAlreadyExistsException() {
    AccountDto accountDto = new AccountDto(1L, 1L, new Date(), "Some account name");
    when(accountRepository.exists(eq(accountDto.getId()))).thenReturn(true);
    service.addAccount(accountDto);
  }

  @Test
  public void testUpdateAccount() {
    AccountDto accountDto = new AccountDto(1L, 1L, new Date(), "Some account name");
    Account account = new Account(accountDto.getId(), new Customer(accountDto.getCustomer()), accountDto.getOpened(),
        accountDto.getName());
    when(accountRepository.exists(eq(accountDto.getId()))).thenReturn(true);
    when(accountRepository.save(eq(account))).thenReturn(account);
    AccountDto updatedDto = service.updateAccount(accountDto);
    assertEquals("Dto are not equals", accountDto, updatedDto);
    verify(accountRepository).save(eq(account));
    verify(accountRepository).exists(eq(accountDto.getId()));
    verifyZeroInteractions(customerRepository);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testUpdateAccount_ResourceNotFoundException() {
    AccountDto accountDto = new AccountDto(1L, 1L, new Date(), "Some account name");
    when(accountRepository.exists(eq(accountDto.getId()))).thenReturn(false);
    service.updateAccount(accountDto);
  }

  @Test
  public void testDeleteAccount() {
    Long accountId = 1L;
    when(accountRepository.exists(eq(accountId))).thenReturn(true);
    service.deleteAccount(accountId);
    verify(accountRepository).delete(eq(accountId));
    verify(accountRepository).exists(eq(accountId));
    verifyZeroInteractions(customerRepository);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testDeleteAccount_ResourceNotFoundException() {
    Long accountId = 1L;
    when(accountRepository.exists(eq(accountId))).thenReturn(false);
    service.deleteAccount(accountId);
  }

}

