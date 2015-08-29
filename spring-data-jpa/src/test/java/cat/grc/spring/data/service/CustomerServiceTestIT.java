/**
 * 
 */
package cat.grc.spring.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import cat.grc.spring.data.EntityManagerConfiguration;
import cat.grc.spring.data.Gender;
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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EntityManagerConfiguration.class})
@Transactional
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class})
@DatabaseSetup("CustomerServiceIT_setup.xml")
public class CustomerServiceTestIT {

  @Autowired
  private CustomerService service;

  @Autowired
  private EntityManager entityManager;

  @Test
  public void testFindAllCustomers() {
    int page = 0;
    int size = 15;
    Collection<CustomerDto> customers = service.findAllCustomers(page, size);
    assertFalse("Expected at least one customer", customers.isEmpty());
  }

  @Test
  public void testFindCustomerById() {
    Long customerId = 1L;
    CustomerDto customer = service.findCustomerById(customerId);
    assertEquals("Expected the same customerId", customerId, customer.getId());
    assertEquals("Expected the same firstName", "Bart", customer.getFirstName());
    assertEquals("Expected the same middleName", "Jojo", customer.getMiddleName());
    assertEquals("Expected the same lastName", "Simpson", customer.getLastName());
    assertEquals("Expected the same Gender", Gender.MALE, customer.getGender());
    assertEquals("Expected the same Phone", "555-7666", customer.getPhoneNumber());
    assertEquals("Expected the same Email", "bart@simpson.com", customer.getEmail());
    assertEquals("Expected the same Address", "742 Evergreen Terrace", customer.getAddress());
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testFindCustomerById_ResourceNotFoundException() {
    Long customerId = 999999L;
    service.findCustomerById(customerId);
  }

  @Test
  @ExpectedDatabase(value = "CustomerServiceIT.testAddCustomer.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testAddCustomer() {
    CustomerDto customer = new CustomerDto(null, "Homer", "Jay", "Simpson", Gender.MALE, "homer@simpson.com",
        "555-7666", "742 Evergreen Terrace");
    CustomerDto savedCustomer = service.addCustomer(customer);
    assertNotNull("Expected a saved customer object returned", savedCustomer);
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testAddCustomer_ResourceAlreadyExistsException() {
    CustomerDto customer = new CustomerDto(1L, "Bart", "Jojo", "Simpson", Gender.MALE, "bart@simpson.com", "555-7666",
        "742 Evergreen Terrace");
    service.addCustomer(customer);
  }

  @Test
  @ExpectedDatabase(value = "CustomerServiceIT.testUpdateCustomer.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testUpdateCustomer() {
    CustomerDto replaceBartForLisa = new CustomerDto(1L, "Lisa", "Marie", "Simpson", Gender.FEMALE, "lisa@simpson.com",
        "888-7666", "743 Evergreen Terrace");
    CustomerDto lisa = service.updateCustomer(replaceBartForLisa);
    assertNotNull("Expected updated customer", lisa);
    assertEquals("Expected Lisa Simpson", replaceBartForLisa, lisa);
    entityManager.flush();
  }

  @Test(expected = CustomerWithAccountsException.class)
  public void testDeleteCustomer_CustomerWithAccountsException() {
    service.deleteCustomer(1L);
  }

  @Test(expected = CustomerWithOrdersException.class)
  public void testDeleteCustomer_CustomerWithOrdersException() {
    service.deleteCustomer(2L);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testUpdateCustomer_ResourceNotFoundException() {
    CustomerDto replaceBartForLisa = new CustomerDto(9999L, "Lisa", "Marie", "Simpson", Gender.FEMALE,
        "lisa@simpson.com", "888-7666", "743 Evergreen Terrace");
    service.updateCustomer(replaceBartForLisa);
  }

  @Test
  @ExpectedDatabase(value = "CustomerServiceIT.testDeleteCustomer.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testDeleteCustomer() {
    service.deleteCustomer(3L);
    entityManager.flush();
  }

  @Test
  public void testFindAllAccountsByCustomerId() {
    Long customerId = 1L;
    Collection<AccountDto> accounts = service.findAllAccountsByCustomerId(customerId, 0, 15);
    assertFalse("Expected at least one account", accounts.isEmpty());
    assertEquals("Expected at least one account", 2, accounts.size());
    assertEquals("Expected the same customerId", customerId, accounts.iterator().next().getCustomerId());
  }

  @Test
  public void testFindAccountById() {
    Long accountId = 1L;
    AccountDto account = service.findAccountById(accountId);
    assertEquals("Expected the same accountId", accountId, account.getId());
    assertEquals("Expected the same customerId", 1L, account.getCustomerId().intValue());
    Date expectedOpened =
        Date.from(LocalDate.of(2015, 8, 26).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    assertEquals("Expected the same date opened", expectedOpened, account.getOpened());
    assertEquals("Expected the same account name", "Bart Account", account.getName());
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testFindAccountById_ResourceNotFoundException() {
    Long customerId = 999999L;
    service.findAccountById(customerId);
  }

  @Test
  @ExpectedDatabase(value = "CustomerServiceIT.testAddAccount.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testAddAccount() {
    Date opened = Date.from(LocalDate.of(2015, 8, 29).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    AccountDto account = new AccountDto(null, 1L, opened, "Second account for Bart");
    AccountDto savedAccount = service.addAccount(account);
    assertNotNull("Expected a saved account returned", savedAccount);
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testAddAccount_ResourceAlreadyExistsException() {
    Date opened = Date.from(LocalDate.of(2015, 8, 28).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    AccountDto account = new AccountDto(1L, 1L, opened, "Second account for Bart");
    service.addAccount(account);
  }

  @Test
  @ExpectedDatabase(value = "CustomerServiceIT.testUpdateAccount.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testUpdateAccount() {
    Date opened = Date.from(LocalDate.of(2015, 8, 29).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    AccountDto account = new AccountDto(1L, 1L, opened, "Another name");
    AccountDto updatedAccount = service.updateAccount(account);
    assertEquals("Expected the same account updated", account, updatedAccount);
    entityManager.flush();
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testUpdateAccount_ResourceNotFoundException() {
    AccountDto account = new AccountDto(99999L, 1L, new Date(), "Another name");
    service.updateAccount(account);
  }

  @Test(expected = AccountWithTransactionsException.class)
  public void testDeleteAccount_AccountWithTransactionsException() {
    Long accountId = 1L;
    service.deleteAccount(accountId);
  }

  @Test
  @ExpectedDatabase(value = "CustomerServiceIT.testDeleteAccount.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testDeleteAccount() {
    Long accountId = 2L;
    service.deleteAccount(accountId);
    entityManager.flush();
  }

}
