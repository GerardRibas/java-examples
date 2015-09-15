/**
 * 
 */
package cat.grc.spring.mvc.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

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

import cat.grc.spring.data.dto.TransactionTypeDto;
import cat.grc.spring.mvc.rest.Application;
import cat.grc.spring.mvc.rest.ErrorResource;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class TransactionTypeControllerTestIT {

  private static final String CONTROLLER_URL = "http://localhost:8080/types";

  private RestTemplate restTemplate;

  private ObjectMapper objectMapper;

  @Before
  public void before() {
    objectMapper = new ObjectMapper();
    restTemplate = new RestTemplate();
  }

  @Test
  public void testGetTransactionTypes() {
    ResponseEntity<TransactionTypeDto[]> response =
        restTemplate.getForEntity(CONTROLLER_URL, TransactionTypeDto[].class);
    assertEquals("Expected OK Status", HttpStatus.OK, response.getStatusCode());
    assertTrue("Expected transaction types on it", response.getBody().length >= 1);
  }

  @Test
  public void testCreateTransactionType() {
    TransactionTypeDto type = new TransactionTypeDto(null, "Credit Card");
    ResponseEntity<TransactionTypeDto> response =
        restTemplate.postForEntity(CONTROLLER_URL, type, TransactionTypeDto.class);
    assertEquals("Expected OK Status", HttpStatus.CREATED, response.getStatusCode());
    assertNotNull("Expected an id generated", response.getBody().getCode());
  }

  @Test
  public void testCreateTransactionType_ResourceAlreadyExistsException()
      throws JsonParseException, JsonMappingException, IOException {
    TransactionTypeDto type = new TransactionTypeDto(1L, "Credit Card");
    try {
      restTemplate.postForEntity(CONTROLLER_URL, type, TransactionTypeDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected OK Status", HttpStatus.CONFLICT, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E001", error.getCode());
      assertEquals("Expected the same message", "TransactionType=1 already exists", error.getMessage());
    }
  }

  @Test
  public void testFindTransactionType() {
    ResponseEntity<TransactionTypeDto> response =
        restTemplate.getForEntity(CONTROLLER_URL + "/1", TransactionTypeDto.class);
    assertEquals("Expected OK Status", HttpStatus.OK, response.getStatusCode());
    assertEquals("Expected transaction type 1", 1L, response.getBody().getCode().longValue());
  }

  @Test
  public void testFindTransactionType_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.getForEntity(CONTROLLER_URL + "/1", TransactionTypeDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected NOT FOUND Status", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
    }
  }

  @Test
  public void testUpdateTransactionType() {
    TransactionTypeDto type = new TransactionTypeDto(1L, "Credit Card");
    restTemplate.put(CONTROLLER_URL + "/1", type);
  }

  @Test
  public void testUpdateTransactionType_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    TransactionTypeDto type = new TransactionTypeDto(999L, "Credit Card");
    try {
      restTemplate.put(CONTROLLER_URL + "/999", type);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected NOT FOUND Status", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
    }
  }

  @Test
  public void testDeleteTransactionType() {
    restTemplate.delete(CONTROLLER_URL + "/2");
  }

  @Test
  public void testDeleteTransactionType_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.delete(CONTROLLER_URL + "/999");
    } catch (HttpClientErrorException e) {
      assertEquals("Expected NOTFOUND Status", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "Transaction type does not exists with id=999", error.getMessage());
    }
  }

  @Test
  public void testDeleteTransactionType_TransactionTypeHasTransactionsException()
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.delete(CONTROLLER_URL + "/1");
    } catch (HttpClientErrorException e) {
      assertEquals("Expected CONFLICT Status", HttpStatus.CONFLICT, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E008", error.getCode());
      assertEquals("Expected the same message", "Transaction type 1 has transactions associated", error.getMessage());
    }
  }

}
