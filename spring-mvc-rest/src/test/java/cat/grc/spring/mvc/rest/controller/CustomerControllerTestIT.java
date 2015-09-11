package cat.grc.spring.mvc.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cat.grc.spring.data.Gender;
import cat.grc.spring.data.dto.AccountDto;
import cat.grc.spring.data.dto.CustomerDto;
import cat.grc.spring.mvc.rest.Application;
import cat.grc.spring.mvc.rest.ErrorResource;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class CustomerControllerTestIT {

  private static final String CONTROLLER_URL = "http://localhost:8080/customers";

  private RestTemplate restTemplate;

  private ObjectMapper objectMapper;

  @Before
  public void before() {
    objectMapper = new ObjectMapper();
    restTemplate = new RestTemplate();
  }

  @Test
  public void testGetCustomers() {
    ResponseEntity<CustomerDto[]> responseEntity = restTemplate.getForEntity(CONTROLLER_URL, CustomerDto[].class);
    CustomerDto[] customers = responseEntity.getBody();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    assertEquals("Expected 15 customers", 15, customers.length);
  }

  @Test
  public void testCreateCustomer() {
    CustomerDto customer = new CustomerDto(null, "Gerard", "Ribas", "Canals", Gender.MALE,
        "gerardribascanals@myemail.com", "55512345", "My Address");
    ResponseEntity<CustomerDto> response = restTemplate.postForEntity(CONTROLLER_URL, customer, CustomerDto.class);
    assertEquals("Expected created http status code", HttpStatus.CREATED, response.getStatusCode());
    assertNotNull("Expected an id populated", response.getBody().getId());
  }

  @Test
  public void testCreateCustomer_ResourceAlreadyExistsException()
      throws JsonParseException, JsonMappingException, IOException {
    CustomerDto customer = new CustomerDto(1L, "Gerard", "Ribas", "Canals", Gender.MALE,
        "gerardribascanals@myemail.com", "55512345", "My Address");
    try {
      restTemplate.postForEntity(CONTROLLER_URL, customer, CustomerDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected created http status code", HttpStatus.CONFLICT, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E001", error.getCode());
      assertEquals("Expected the same message", "CustomerId=1 already exists", error.getMessage());
    }
  }

  @Test
  public void testFindCustomer() {
    ResponseEntity<CustomerDto> response = restTemplate.getForEntity(CONTROLLER_URL + "/1", CustomerDto.class);
    assertEquals("Expected OK status code", HttpStatus.OK, response.getStatusCode());
    assertEquals("Expected the same customerId", 1L, response.getBody().getId().longValue());
  }

  @Test
  public void testFindCustomer_ResourceAlreadyExistsException()
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.getForEntity(CONTROLLER_URL + "/999999", CustomerDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected created http status code", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "No Customer found for id=999999", error.getMessage());
    }
  }

  @Test
  public void testUpdateCustomer() {
    CustomerDto customer = new CustomerDto(1L, "Gerard", "Ribas", "Canals", Gender.MALE,
        "gerardribascanals@myemail.com", "55512345", "My Address");
    restTemplate.put(CONTROLLER_URL + "/1", customer);
    ResponseEntity<CustomerDto> response = restTemplate.getForEntity(CONTROLLER_URL + "/1", CustomerDto.class);
    assertEquals("Expected created http status code", HttpStatus.OK, response.getStatusCode());
    assertEquals("Expected the updated customer", customer, response.getBody());
  }

  @Test
  public void testUpdateCustomer_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    CustomerDto customer = new CustomerDto(999L, "Gerard", "Ribas", "Canals", Gender.MALE,
        "gerardribascanals@myemail.com", "55512345", "My Address");
    try {
      restTemplate.put(CONTROLLER_URL + "/999", customer);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected created http status code", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "CustomerId=999 does not exists", error.getMessage());
    }
  }

  @Test
  public void testDeleteCustomer() throws JsonParseException, JsonMappingException, IOException {
    restTemplate.delete(CONTROLLER_URL + "/100");
    try {
      restTemplate.getForEntity(CONTROLLER_URL + "/100", CustomerDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected created http status code", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "No Customer found for id=100", error.getMessage());
    }
  }

  @Test
  public void testDeleteCustomer_CustomerWithAccountsException()
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.delete(CONTROLLER_URL + "/80");
    } catch (HttpClientErrorException e) {
      assertEquals("Expected created http status code", HttpStatus.CONFLICT, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E005", error.getCode());
      assertEquals("Expected the same message", "Customer 80 has accounts, please delete first the accounts",
          error.getMessage());
    }
  }

  @Test
  public void testDeleteCustomer_CustomerWithOrdersException()
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.delete(CONTROLLER_URL + "/5");
    } catch (HttpClientErrorException e) {
      assertEquals("Expected created http status code", HttpStatus.CONFLICT, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E004", error.getCode());
      assertEquals("Expected the same message", "Customer 5 has orders, please delete first the orders",
          error.getMessage());
    }
  }

  @Test
  public void testGetAccountsOfACustomer() {
    ResponseEntity<AccountDto[]> responseEntity =
        restTemplate.getForEntity(CONTROLLER_URL + "/3/accounts", AccountDto[].class);
    AccountDto[] accounts = responseEntity.getBody();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    assertEquals("Expected 1 account", 1, accounts.length);
  }

  @Test
  public void testCreateAccount() {
    AccountDto account = new AccountDto(null, 2L, new Date(), "Personal Account");
    ResponseEntity<AccountDto> responseEntity =
        restTemplate.postForEntity(CONTROLLER_URL + "/1/accounts", account, AccountDto.class);
    assertEquals("Expected status created", HttpStatus.CREATED, responseEntity.getStatusCode());
    assertNotNull("Expected an id for the new account created", responseEntity.getBody().getId());
  }

  @Test
  public void testCreateAccount_ResourceAlreadyExistsException()
      throws JsonParseException, JsonMappingException, IOException {
    AccountDto account = new AccountDto(1L, 2L, new Date(), "Personal Account");
    try {
      restTemplate.postForEntity(CONTROLLER_URL + "/1/accounts", account, AccountDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected created http status code", HttpStatus.CONFLICT, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E001", error.getCode());
      assertEquals("Expected the same message", "AccountId=1 already exists", error.getMessage());
    }
  }

  @Test
  public void testFindAccount() {
    ResponseEntity<AccountDto> response = restTemplate.getForEntity(CONTROLLER_URL + "/1/accounts/1", AccountDto.class);
    assertEquals("Expected OK status code", HttpStatus.OK, response.getStatusCode());
    assertEquals("Expected the same accountId", 1L, response.getBody().getId().longValue());
  }

  @Test
  public void testFindAccount_ResourceNotFoundException() throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.getForEntity(CONTROLLER_URL + "/1/accounts/9999", AccountDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected created http status code", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "No Account found for id=9999", error.getMessage());
    }
  }

  @Test
  public void testUpdateAccount() {
    AccountDto account = new AccountDto(1L, 2L, new Date(), "Personal Account");
    restTemplate.put(CONTROLLER_URL + "/2/accounts/1", account);
    ResponseEntity<AccountDto> response = restTemplate.getForEntity(CONTROLLER_URL + "/2/accounts/1", AccountDto.class);
    assertEquals("Expected OK status code", HttpStatus.OK, response.getStatusCode());
    assertEquals("Expected the same account", account, response.getBody());
  }

  @Test
  public void testUpdateAccount_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    AccountDto account = new AccountDto(999L, 2L, new Date(), "Personal Account");
    try {
      restTemplate.put(CONTROLLER_URL + "/2/accounts/999", account);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected created http status code", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "AccountId=999 does not exists", error.getMessage());
    }
  }

  @Test
  public void testDeleteAccount() throws JsonParseException, JsonMappingException, IOException {
    restTemplate.delete(CONTROLLER_URL + "/52/accounts/52");
    try {
      restTemplate.getForEntity(CONTROLLER_URL + "/52/accounts/52", AccountDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected created http status code", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "No Account found for id=52", error.getMessage());
    }
  }

  @Test
  public void testDeleteAccount_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.delete(CONTROLLER_URL + "/52/accounts/999");
    } catch (HttpClientErrorException e) {
      assertEquals("Expected created http status code", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "No Account found for id=999", error.getMessage());
    }
  }

  @Test
  public void testDeleteAccount_AccountWithTransactionsException()
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.delete(CONTROLLER_URL + "/1/accounts/1");
    } catch (HttpClientErrorException e) {
      assertEquals("Expected created http status code", HttpStatus.CONFLICT, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E006", error.getCode());
      assertEquals("Expected the same message",
          "Account 1 has transactions associated, please revise it before deleting the account", error.getMessage());
    }
  }

}
