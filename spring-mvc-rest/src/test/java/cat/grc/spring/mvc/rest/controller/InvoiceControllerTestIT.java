/**
 * 
 */
package cat.grc.spring.mvc.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
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

import cat.grc.spring.data.dto.InvoiceDto;
import cat.grc.spring.data.dto.InvoiceLineItemDto;
import cat.grc.spring.mvc.rest.Application;
import cat.grc.spring.mvc.rest.ErrorResource;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class InvoiceControllerTestIT {

  private static final String BASE_URL = "http://localhost:8080";

  private RestTemplate restTemplate;

  private ObjectMapper objectMapper;

  @Before
  public void before() {
    objectMapper = new ObjectMapper();
    restTemplate = new RestTemplate();
  }

  @Test
  public void testGetInvoicesByCustomer() {
    ResponseEntity<InvoiceDto[]> responseEntity =
        restTemplate.getForEntity(BASE_URL + "/customers/1/invoices", InvoiceDto[].class);
    assertEquals("Expected OK Status", HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Expected 1 invoice", 1, responseEntity.getBody().length);
  }

  @Test
  @DirtiesContext
  public void testCreateInvoice() {
    InvoiceDto invoice = new InvoiceDto(2L, new Date());
    Collection<InvoiceLineItemDto> items = new ArrayList<>();
    items.add(new InvoiceLineItemDto(null, 6L, invoice, 6L, null, 1L, new BigDecimal("26.455"), null, null, null));
    items.add(new InvoiceLineItemDto(null, 7L, invoice, 7L, null, 2L, new BigDecimal("36.794"), null, null, null));
    items.add(new InvoiceLineItemDto(null, 8L, invoice, 8L, null, 1L, new BigDecimal("9.538"), null, null, null));
    items.add(new InvoiceLineItemDto(null, 9L, invoice, 9L, null, 3L, new BigDecimal("8.478"), null, null, null));
    items.add(new InvoiceLineItemDto(null, 10L, invoice, 19L, null, 10L, new BigDecimal("157.504"), null, null, null));
    invoice.setLines(items);

    ResponseEntity<InvoiceDto> response = restTemplate.postForEntity(BASE_URL + "/invoices", invoice, InvoiceDto.class);
    assertEquals("Expected STATUS Created", HttpStatus.CREATED, response.getStatusCode());
    assertNotNull("Expected an invoice number created", response.getBody().getNumber());
  }

  @Test
  @DirtiesContext
  public void testCreateInvoiceCustomersPath() {
    InvoiceDto invoice = new InvoiceDto(3L, new Date());
    Collection<InvoiceLineItemDto> items = new ArrayList<>();
    items.add(new InvoiceLineItemDto(null, 11L, invoice, 11L, null, 1L, new BigDecimal("64.707"), null, null, null));
    items.add(new InvoiceLineItemDto(null, 12L, invoice, 12L, null, 4L, new BigDecimal("30.348"), null, null, null));
    items.add(new InvoiceLineItemDto(null, 13L, invoice, 13L, null, 7L, new BigDecimal("383.11"), null, null, null));
    invoice.setLines(items);
    ResponseEntity<InvoiceDto> response =
        restTemplate.postForEntity(BASE_URL + "/customers/3/invoices", invoice, InvoiceDto.class);
    assertEquals("Expected STATUS Created", HttpStatus.CREATED, response.getStatusCode());
    assertNotNull("Expected an invoice number created", response.getBody().getNumber());
  }


  @Test
  public void testCreateInvoice_ResourceAlreadyExistsException()
      throws JsonParseException, JsonMappingException, IOException {
    InvoiceDto invoice = new InvoiceDto(2L, new Date());
    invoice.setNumber(1L);
    try {
      restTemplate.postForEntity(BASE_URL + "/customers/2/invoices", invoice, InvoiceDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected CONFLICT status", HttpStatus.CONFLICT, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E001", error.getCode());
      assertEquals("Expected the same message", "Invoice already exists with id=1", error.getMessage());
    }
  }

  @Test
  public void testFindInvoice() {
    testFindInvoice("invoices/1");
  }

  @Test
  public void testFindInvoiceCustomersPath() {
    testFindInvoice("/customers/1/invoices/1");
  }

  @Test
  public void testFindInvoice_ResourceNotFoundException() throws JsonParseException, JsonMappingException, IOException {
    testFindInvoice_ResourceNotFoundException("invoices/999");
  }

  @Test
  public void testFindInvoiceCustomersPath_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    testFindInvoice_ResourceNotFoundException("/customers/1/invoices/999");
  }

  @Test
  @DirtiesContext
  public void testDeleteInvoice() throws JsonParseException, JsonMappingException, IOException {
    testDeleteInvoice("/invoices/2");
  }

  @Test
  @DirtiesContext
  public void testDeleteInvoiceCustomersPath() throws JsonParseException, JsonMappingException, IOException {
    testDeleteInvoice("/customers/2/invoices/2");
  }

  @Test
  public void testDeleteInvoice_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    testDeleteInvoice_ResourceNotFoundException("/invoices/999");
  }

  @Test
  public void testDeleteInvoiceCustomersPath_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    testDeleteInvoice_ResourceNotFoundException("/customers/2/invoices/999");
  }

  @Test
  public void testFindInvoiceLineItems() {
    testFindInvoiceLineItems("/invoices/1/lines");
  }

  @Test
  public void testFindInvoiceLineItemsCustomersPath() {
    testFindInvoiceLineItems("/customers/1/invoices/1/lines");
  }

  @Test
  public void testGetInvoicesByOrder() {
    testGetInvoicesByOrder("/orders/1/invoices");
  }

  @Test
  public void testGetInvoicesByOrderCustomersPath() {
    testGetInvoicesByOrder("/customers/1/orders/1/invoices");
  }

  private void testGetInvoicesByOrder(String url) {
    ResponseEntity<InvoiceDto[]> response = restTemplate.getForEntity(BASE_URL + url, InvoiceDto[].class);
    assertEquals("Expected OK status", HttpStatus.OK, response.getStatusCode());
    assertEquals("Expected an invoice", 1, response.getBody().length);
  }

  private void testFindInvoiceLineItems(String url) {
    ResponseEntity<InvoiceLineItemDto[]> response =
        restTemplate.getForEntity(BASE_URL + url, InvoiceLineItemDto[].class);
    assertEquals("Expected OK status", HttpStatus.OK, response.getStatusCode());
    assertEquals("Expected 5 items", 5, response.getBody().length);
  }

  private void testDeleteInvoice_ResourceNotFoundException(String url)
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.delete(BASE_URL + url);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected NOTFOUND status", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "Invoice does not exists with id=999", error.getMessage());
    }
  }


  private void testDeleteInvoice(String url) throws JsonParseException, JsonMappingException, IOException {
    restTemplate.delete(BASE_URL + url);
    try {
      restTemplate.getForEntity(BASE_URL + url, InvoiceDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected NOTFOUND status", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "Invoice does not exists with id=2", error.getMessage());
    }
  }

  private void testFindInvoice(String url) {
    Date created = Date.from(LocalDate.of(2015, 8, 14).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    InvoiceDto invoice = new InvoiceDto(1L, created);
    invoice.setNumber(1L);
    ResponseEntity<InvoiceDto> response = restTemplate.getForEntity(BASE_URL + url, InvoiceDto.class);
    assertEquals("Expected STATUS Created", HttpStatus.OK, response.getStatusCode());
    assertEquals("Expected an invoice number created", invoice, response.getBody());
  }

  private void testFindInvoice_ResourceNotFoundException(String url)
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.getForEntity(BASE_URL + url, InvoiceDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected NOTFOUND status", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "Invoice does not exists with id=999", error.getMessage());
    }
  }


}
