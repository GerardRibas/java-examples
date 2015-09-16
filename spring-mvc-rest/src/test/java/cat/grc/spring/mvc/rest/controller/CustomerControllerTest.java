package cat.grc.spring.mvc.rest.controller;

import static cat.grc.spring.mvc.rest.controller.TestUtil.APPLICATION_JSON_UTF8;
import static cat.grc.spring.mvc.rest.controller.TestUtil.createExceptionResolver;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cat.grc.spring.data.Gender;
import cat.grc.spring.data.dto.AccountDto;
import cat.grc.spring.data.dto.CustomerDto;
import cat.grc.spring.data.exception.AccountWithTransactionsException;
import cat.grc.spring.data.exception.CustomerWithAccountsException;
import cat.grc.spring.data.exception.CustomerWithOrdersException;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.service.CustomerService;
import cat.grc.spring.mvc.rest.Application;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ContextConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class CustomerControllerTest {

  private MockMvc mvc;

  private ObjectMapper objectMapper;

  @Mock
  private CustomerService service;

  @Before
  public void before() {
    objectMapper = new ObjectMapper();
    MockitoAnnotations.initMocks(this);
    CustomerController controller = new CustomerController();
    controller.setService(service);
    mvc = standaloneSetup(controller).setHandlerExceptionResolvers(createExceptionResolver()).build();
  }

  @Test
  public void testGetCustomers() throws Exception {
    CustomerDto customer = new CustomerDto(1L, "Quentin", "Brandon", "Stout", Gender.MALE,
        "tristique.senectus.et@dolorsit.edu", "(016977) 7773", "794-6546 Dis Street");
    when(service.findAllCustomers(eq(0), eq(15))).thenReturn(Arrays.asList(customer));
    mvc.perform(get("/customers").accept(APPLICATION_JSON_UTF8)).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].firstName", is(customer.getFirstName())))
        .andExpect(jsonPath("$[0].middleName", is(customer.getMiddleName())))
        .andExpect(jsonPath("$[0].lastName", is(customer.getLastName())))
        .andExpect(jsonPath("$[0].gender", is(customer.getGender().name())))
        .andExpect(jsonPath("$[0].email", is(customer.getEmail())))
        .andExpect(jsonPath("$[0].phoneNumber", is(customer.getPhoneNumber())))
        .andExpect(jsonPath("$[0].address", is(customer.getAddress())));
    verify(service).findAllCustomers(eq(0), eq(15));
  }

  @Test
  public void testCreateCustomer() throws JsonProcessingException, Exception {
    CustomerDto customer = new CustomerDto(null, "Quentin", "Brandon", "Stout", Gender.MALE,
        "tristique.senectus.et@dolorsit.edu", "(016977) 7773", "794-6546 Dis Street");
    CustomerDto savedCustomer = new CustomerDto(1L, "Quentin", "Brandon", "Stout", Gender.MALE,
        "tristique.senectus.et@dolorsit.edu", "(016977) 7773", "794-6546 Dis Street");
    when(service.addCustomer(customer)).thenReturn(savedCustomer);

    mvc.perform(post("/customers").accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(customer))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1))).andExpect(jsonPath("$.firstName", is(savedCustomer.getFirstName())))
        .andExpect(jsonPath("$.middleName", is(savedCustomer.getMiddleName())))
        .andExpect(jsonPath("$.lastName", is(savedCustomer.getLastName())))
        .andExpect(jsonPath("$.gender", is(savedCustomer.getGender().name())))
        .andExpect(jsonPath("$.email", is(savedCustomer.getEmail())))
        .andExpect(jsonPath("$.phoneNumber", is(savedCustomer.getPhoneNumber())))
        .andExpect(jsonPath("$.address", is(savedCustomer.getAddress())));

    verify(service).addCustomer(eq(customer));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testCreateCustomer_ResourceAlreadyExistsException() throws JsonProcessingException, Exception {
    CustomerDto customer = new CustomerDto(1L, "Quentin", "Brandon", "Stout", Gender.MALE,
        "tristique.senectus.et@dolorsit.edu", "(016977) 7773", "794-6546 Dis Street");
    when(service.addCustomer(customer)).thenThrow(ResourceAlreadyExistsException.class);

    mvc.perform(post("/customers").accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(customer))).andExpect(status().isConflict())
        .andExpect(jsonPath("$.code", is("E001")));

    verify(service).addCustomer(eq(customer));
  }

  @Test
  public void testFindCustomer() throws Exception {
    CustomerDto customer = new CustomerDto(1L, "Quentin", "Brandon", "Stout", Gender.MALE,
        "tristique.senectus.et@dolorsit.edu", "(016977) 7773", "794-6546 Dis Street");
    when(service.findCustomerById(eq(customer.getId()))).thenReturn(customer);
    mvc.perform(get("/customers/1").accept(APPLICATION_JSON_UTF8)).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1))).andExpect(jsonPath("$.firstName", is(customer.getFirstName())))
        .andExpect(jsonPath("$.middleName", is(customer.getMiddleName())))
        .andExpect(jsonPath("$.lastName", is(customer.getLastName())))
        .andExpect(jsonPath("$.gender", is(customer.getGender().name())))
        .andExpect(jsonPath("$.email", is(customer.getEmail())))
        .andExpect(jsonPath("$.phoneNumber", is(customer.getPhoneNumber())))
        .andExpect(jsonPath("$.address", is(customer.getAddress())));
    verify(service).findCustomerById(eq(customer.getId()));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindCustomer_ResourceNotFoundException() throws Exception {
    when(service.findCustomerById(eq(1L))).thenThrow(ResourceNotFoundException.class);
    mvc.perform(get("/customers/1").accept(APPLICATION_JSON_UTF8)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("E002")));
    verify(service).findCustomerById(eq(1L));
  }

  @Test
  public void testUpdateCustomer() throws JsonProcessingException, Exception {
    CustomerDto customer = new CustomerDto(1L, "Quentin", "Brandon", "Stout", Gender.MALE,
        "tristique.senectus.et@dolorsit.edu", "(016977) 7773", "794-6546 Dis Street");
    when(service.updateCustomer(eq(customer))).thenReturn(customer);
    mvc.perform(put("/customers/1").accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(customer))).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1))).andExpect(jsonPath("$.firstName", is(customer.getFirstName())))
        .andExpect(jsonPath("$.middleName", is(customer.getMiddleName())))
        .andExpect(jsonPath("$.lastName", is(customer.getLastName())))
        .andExpect(jsonPath("$.gender", is(customer.getGender().name())))
        .andExpect(jsonPath("$.email", is(customer.getEmail())))
        .andExpect(jsonPath("$.phoneNumber", is(customer.getPhoneNumber())))
        .andExpect(jsonPath("$.address", is(customer.getAddress())));
    verify(service).updateCustomer(eq(customer));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testUpdateCustomer_ResourceNotFoundException() throws JsonProcessingException, Exception {
    CustomerDto customer = new CustomerDto(1L, "Quentin", "Brandon", "Stout", Gender.MALE,
        "tristique.senectus.et@dolorsit.edu", "(016977) 7773", "794-6546 Dis Street");
    when(service.updateCustomer(eq(customer))).thenThrow(ResourceNotFoundException.class);
    mvc.perform(put("/customers/1").accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(customer))).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("E002")));
    verify(service).updateCustomer(eq(customer));
  }

  @Test
  public void testDeleteCustomer() throws Exception {
    Long customerId = 1L;
    mvc.perform(delete("/customers/1")).andExpect(status().isNoContent());
    verify(service).deleteCustomer(eq(customerId));
  }

  @Test
  public void testDeleteCustomer_ResourceNotFoundException() throws Exception {
    Long customerId = 1L;
    doThrow(ResourceNotFoundException.class).when(service).deleteCustomer(eq(customerId));
    mvc.perform(delete("/customers/1")).andExpect(status().isNotFound()).andExpect(jsonPath("$.code", is("E002")));
    verify(service).deleteCustomer(eq(customerId));
  }

  @Test
  public void testDeleteCustomer_CustomerWithOrdersException() throws Exception {
    Long customerId = 1L;
    doThrow(CustomerWithOrdersException.class).when(service).deleteCustomer(eq(customerId));
    mvc.perform(delete("/customers/1")).andExpect(status().isConflict()).andExpect(jsonPath("$.code", is("E004")));
    verify(service).deleteCustomer(eq(customerId));
  }

  @Test
  public void testDeleteCustomer_CustomerWithAccountsException() throws Exception {
    Long customerId = 1L;
    doThrow(CustomerWithAccountsException.class).when(service).deleteCustomer(eq(customerId));
    mvc.perform(delete("/customers/1")).andExpect(status().isConflict()).andExpect(jsonPath("$.code", is("E005")));
    verify(service).deleteCustomer(eq(customerId));
  }

  @Test
  public void testGetAccountsOfACustomer() throws Exception {
    Date opened = new Date();
    AccountDto account = new AccountDto(1L, 1L, opened, "Personal Account");
    when(service.findAllAccountsByCustomerId(eq(account.getCustomerId()), eq(0), eq(15)))
        .thenReturn(Arrays.asList(account));
    mvc.perform(get("/customers/1/accounts").accept(APPLICATION_JSON_UTF8)).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].customerId", is(1))).andExpect(jsonPath("$[0].opened", is(opened.getTime())))
        .andExpect(jsonPath("$[0].name", is(account.getName())));
    verify(service).findAllAccountsByCustomerId(eq(account.getCustomerId()), eq(0), eq(15));
  }

  @Test
  public void testCreateAccount() throws Exception {
    Date opened = new Date();
    AccountDto account = new AccountDto(null, 1L, opened, "Personal Account");
    AccountDto savedAccount = new AccountDto(1L, 1L, opened, "Personal Account");
    when(service.addAccount(eq(account))).thenReturn(savedAccount);
    mvc.perform(post("/customers/1/accounts").accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(account))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1))).andExpect(jsonPath("$.customerId", is(1)))
        .andExpect(jsonPath("$.opened", is(opened.getTime()))).andExpect(jsonPath("$.name", is(account.getName())));
    verify(service).addAccount(eq(account));
  }

  @Test
  public void testFindAccount() throws Exception {
    Date opened = new Date();
    AccountDto account = new AccountDto(1L, 1L, opened, "Personal Account");
    when(service.findAccountById(eq(account.getId()))).thenReturn(account);
    mvc.perform(get("/customers/1/accounts/1").accept(APPLICATION_JSON_UTF8)).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1))).andExpect(jsonPath("$.customerId", is(1)))
        .andExpect(jsonPath("$.opened", is(opened.getTime()))).andExpect(jsonPath("$.name", is(account.getName())));
    verify(service).findAccountById(eq(account.getId()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testFindAccount_ResourceNotFoundException() throws Exception {
    Date opened = new Date();
    AccountDto account = new AccountDto(1L, 1L, opened, "Personal Account");
    when(service.findAccountById(eq(account.getId()))).thenThrow(ResourceNotFoundException.class);
    mvc.perform(get("/customers/1/accounts/1").accept(APPLICATION_JSON_UTF8)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("E002")));
    verify(service).findAccountById(eq(account.getId()));
  }

  @Test
  public void testUpdateAccount() throws Exception {
    Date opened = new Date();
    AccountDto account = new AccountDto(1L, 1L, opened, "Personal Account");
    when(service.updateAccount(account)).thenReturn(account);
    mvc.perform(put("/customers/1/accounts/1").accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(account))).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1))).andExpect(jsonPath("$.customerId", is(1)))
        .andExpect(jsonPath("$.opened", is(opened.getTime()))).andExpect(jsonPath("$.name", is(account.getName())));
    verify(service).updateAccount(account);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateAccount_ResourceNotFoundException() throws Exception {
    Date opened = new Date();
    AccountDto account = new AccountDto(1L, 1L, opened, "Personal Account");
    when(service.updateAccount(account)).thenThrow(ResourceNotFoundException.class);
    mvc.perform(put("/customers/1/accounts/1").accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(account))).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("E002")));
    verify(service).updateAccount(account);
  }

  @Test
  public void testDeleteAccount() throws Exception {
    Long accountId = 1L;
    mvc.perform(delete("/customers/1/accounts/1")).andExpect(status().isNoContent());
    verify(service).deleteAccount(eq(accountId));
  }

  @Test
  public void testDeleteAccount_ResourceNotFoundException() throws Exception {
    Long accountId = 1L;
    doThrow(ResourceNotFoundException.class).when(service).deleteAccount(eq(accountId));
    mvc.perform(delete("/customers/1/accounts/1")).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("E002")));;
    verify(service).deleteAccount(eq(accountId));
  }

  @Test
  public void testDeleteAccount_AccountWithTransactionsException() throws Exception {
    Long accountId = 1L;
    doThrow(AccountWithTransactionsException.class).when(service).deleteAccount(eq(accountId));
    mvc.perform(delete("/customers/1/accounts/1")).andExpect(status().isConflict())
        .andExpect(jsonPath("$.code", is("E006")));;
    verify(service).deleteAccount(eq(accountId));
  }

}
