/**
 * 
 */
package cat.grc.spring.mvc.rest.controller;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cat.grc.spring.data.dto.FinancialTransactionDto;
import cat.grc.spring.mvc.rest.Application;
import cat.grc.spring.mvc.rest.ErrorResource;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class TransactionControllerTestIT {

  private static final String SERVER_URL = "http://localhost:8080";

  private RestTemplate restTemplate;

  private ObjectMapper objectMapper;

  @Before
  public void before() {
    objectMapper = new ObjectMapper();
    restTemplate = new RestTemplate();
  }

  @Test
  public void testFindTransactionsForInvoice() {
    ResponseEntity<FinancialTransactionDto[]> response =
        restTemplate.getForEntity(SERVER_URL + "/invoices/1/transactions", FinancialTransactionDto[].class);
    assertEquals(response.getStatusCode(), HttpStatus.OK);
    assertEquals("Expected at 1 transaction", 1, response.getBody().length);
  }

  @Test
  @DirtiesContext
  public void testCreateTransactionInvoicesPath() {
    testCreateTransaction("/invoices/2/transactions");
  }

  @Test
  @DirtiesContext
  public void testCreateTransaction() {
    testCreateTransaction("/transactions");
  }

  @Test
  public void testFindFinancialTransaction() {
    ResponseEntity<FinancialTransactionDto> response =
        restTemplate.getForEntity(SERVER_URL + "/transactions/1", FinancialTransactionDto.class);
    assertEquals(response.getStatusCode(), HttpStatus.OK);
    assertEquals("Expected the same transaction id", 1L, response.getBody().getId().longValue());
  }

  @Test
  public void testFindFinancialTransaction_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.getForEntity(SERVER_URL + "/transactions/999", FinancialTransactionDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "Transaction does not exists with id=999", error.getMessage());
    }
  }

  @Test
  public void testUpdateFinancialTransaction() {
    FinancialTransactionDto transacton =
        new FinancialTransactionDto(1L, 1L, 1L, 1L, new Date(), new BigDecimal("11.22"));
    restTemplate.put(SERVER_URL + "/transactions/1", transacton);
  }

  @Test
  public void testUpdateFinancialTransaction_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    try {
      FinancialTransactionDto transacton =
          new FinancialTransactionDto(999L, 1L, 1L, 1L, new Date(), new BigDecimal("11.22"));
      restTemplate.put(SERVER_URL + "/transactions/999", transacton);
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "Transaction does not exists with id=999", error.getMessage());
    }
  }

  @Test
  @DirtiesContext
  public void deleteFinancialTransaction() throws JsonParseException, JsonMappingException, IOException {
    restTemplate.delete(SERVER_URL + "/transactions/1");

    try {
      restTemplate.getForEntity(SERVER_URL + "/transactions/1", FinancialTransactionDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "Transaction does not exists with id=1", error.getMessage());
    }

  }

  @Test
  public void deleteFinancialTransaction_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.delete(SERVER_URL + "/transactions/999");
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "Transaction does not exists with id=999", error.getMessage());
    }
  }

  private void testCreateTransaction(String url) {
    FinancialTransactionDto transacton =
        new FinancialTransactionDto(null, 2L, 2L, 1L, new Date(), new BigDecimal("11.22"));

    FinancialTransactionDto expectedTransacton =
        new FinancialTransactionDto(2L, 2L, 2L, 1L, transacton.getTransactionDate(), new BigDecimal("11.22"));

    ResponseEntity<FinancialTransactionDto> response =
        restTemplate.postForEntity(SERVER_URL + url, transacton, FinancialTransactionDto.class);

    assertEquals(response.getStatusCode(), HttpStatus.CREATED);
    assertEquals("Expected the same transaction", expectedTransacton, response.getBody());
  }

}
