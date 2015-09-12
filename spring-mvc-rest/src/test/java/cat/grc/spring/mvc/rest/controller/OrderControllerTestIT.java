/**
 * 
 */
package cat.grc.spring.mvc.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
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

import cat.grc.spring.data.dto.OrderDto;
import cat.grc.spring.data.dto.OrderItemDto;
import cat.grc.spring.data.dto.ProductCategoryDto;
import cat.grc.spring.data.dto.ProductDto;
import cat.grc.spring.mvc.rest.Application;
import cat.grc.spring.mvc.rest.ErrorResource;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class OrderControllerTestIT {

  private static final String BASE_URL = "http://localhost:8080";

  private RestTemplate restTemplate;

  private ObjectMapper objectMapper;

  @Before
  public void before() {
    objectMapper = new ObjectMapper();
    restTemplate = new RestTemplate();
  }

  @Test
  public void testGetOrdersOfACustomer() {
    ResponseEntity<OrderDto[]> responseEntity =
        restTemplate.getForEntity(BASE_URL + "/customers/1/orders", OrderDto[].class);
    OrderDto[] orders = responseEntity.getBody();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    assertEquals("Expected at 1 order", 1, orders.length);
  }

  @Test
  public void testCreateOrder() {
    testCreateOrder("/orders");
  }

  @Test
  public void testCreateOrderCustomersPath() {
    testCreateOrder("/customers/1/orders");
  }

  @Test
  public void testCreateOrder_ResourceAlreadyExistsException()
      throws JsonParseException, JsonMappingException, IOException {
    testCreateOrder_ResourceAlreadyExistsException("/orders");
  }

  @Test
  public void testCreateOrderCustomersPath_ResourceAlreadyExistsException()
      throws JsonParseException, JsonMappingException, IOException {
    testCreateOrder_ResourceAlreadyExistsException("/customers/1/orders");
  }

  @Test
  public void testGetOrder() {
    testGetOrder("/orders/1");
  }

  @Test
  public void testGetOrderCustomersPath() {
    testGetOrder("/customers/1/orders/1");
  }

  @Test
  public void testGetOrder_ResourceNotFoundException() throws JsonParseException, JsonMappingException, IOException {
    testGetOrder_ResourceNotFoundException("/orders/9999");
  }

  @Test
  public void testGetOrderCustomersPath_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    testGetOrder_ResourceNotFoundException("/customers/1/orders/9999");
  }

  @Test
  public void testUpdateOrder() {
    testUpdateOrder("/orders/2");
  }

  @Test
  public void testUpdateOrderCustomersPath() {
    testUpdateOrder("customers/2/orders/2");
  }

  @Test
  public void testUpdateOrder_ResourceNotFoundException() throws JsonParseException, JsonMappingException, IOException {
    testUpdateOrder_ResourceNotFoundException("/orders/9999");
  }

  @Test
  public void testUpdateOrderCustomersPath_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    testUpdateOrder_ResourceNotFoundException("/customers/2/orders/9999");
  }

  @Test
  public void testDeleteOrder() throws JsonParseException, JsonMappingException, IOException {
    testDeleteOrder("/orders/3", "3");
  }

  @Test
  public void testDeleteOrderCustomersPath() throws JsonParseException, JsonMappingException, IOException {
    testDeleteOrder("/customers/4/orders/4", "4");
  }

  @Test
  public void testDeleteOrder_OrderWithInvoicesException()
      throws JsonParseException, JsonMappingException, IOException {
    testDeleteOrder_OrderWithInvoicesException("/orders/1");
  }

  @Test
  public void testDeleteOrderCustomersPath_OrderWithInvoicesException()
      throws JsonParseException, JsonMappingException, IOException {
    testDeleteOrder_OrderWithInvoicesException("customers/1/orders/1");
  }

  @Test
  public void testDeleteOrder_ResourceNotFoundException() throws JsonParseException, JsonMappingException, IOException {
    testDeleteOrder_ResourceNotFoundException("/orders/999");
  }

  @Test
  public void testDeleteOrderCustomersPath_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    testDeleteOrder_ResourceNotFoundException("customers/1/orders/999");
  }

  @Test
  public void testGetOrderItems() {
    testGetOrderItems("/orders/1/items");
  }

  @Test
  public void testGetOrderItemsCustomersPath() {
    testGetOrderItems("/customers/1/orders/1/items");
  }

  @Test
  public void testCreateOrderItem() {
    testCreateOrderItem("/orders/5/items");
  }

  @Test
  public void testCreateOrderItemCustomersPath() {
    testCreateOrderItem("/customers/5/orders/5/items");
  }

  @Test
  public void testCreateOrderItem_ResourceAlreadyExistsException()
      throws JsonParseException, JsonMappingException, IOException {
    testCreateOrderItem_ResourceAlreadyExistsException("/orders/1/items");
  }

  @Test
  public void testCreateOrderItemCustomersPath_ResourceAlreadyExistsException()
      throws JsonParseException, JsonMappingException, IOException {
    testCreateOrderItem_ResourceAlreadyExistsException("/customers/1/orders/1/items");
  }

  @Test
  public void testUpdateOrderItem() {
    testUpdateOrderItem("/orders/5/items/16");
  }

  @Test
  public void testUpdateOrderItemCustomersPath() {
    testUpdateOrderItem("/customers/5/orders/5/items/16");
  }

  @Test
  public void testUpdateOrderItem_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    testUpdateOrderItem_ResourceNotFoundException("/orders/5/items/999");
  }

  @Test
  public void testUpdateOrderItemCustomersPath_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    testUpdateOrderItem_ResourceNotFoundException("/customers/5/orders/5/items/999");
  }

  @Test
  public void testDeleteOrderItem() throws JsonParseException, JsonMappingException, IOException {
    restTemplate.delete(BASE_URL + "/orders/2/items/7");
  }

  @Test
  public void testDeleteOrderItemCustomersPath() throws JsonParseException, JsonMappingException, IOException {
    restTemplate.delete(BASE_URL + "/customers/2/orders/2/items/8");
  }

  @Test
  public void testDeleteOrderItem_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    testDeleteOrderItem_ResourceNotFoundException("/orders/2/items/999");
  }

  @Test
  public void testDeleteOrderItemCustomersPath_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    testDeleteOrderItem_ResourceNotFoundException("/customers/2/orders/2/items/999");
  }

  private void testDeleteOrderItem_ResourceNotFoundException(String url)
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.delete(BASE_URL + url);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected CONFLICT status", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "Order Item not found for id=999", error.getMessage());
    }
  }

  private void testUpdateOrderItem_ResourceNotFoundException(String url)
      throws JsonParseException, JsonMappingException, IOException {
    OrderItemDto item = new OrderItemDto(999L, 5L, new ProductDto(1L), 1L, new BigDecimal("12.22"));
    try {
      restTemplate.put(BASE_URL + url, item);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected CONFLICT status", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "Order Item not found for id=999", error.getMessage());
    }
  }

  private void testUpdateOrderItem(String url) {
    OrderItemDto item = new OrderItemDto(16L, 5L, new ProductDto(1L), 1L, new BigDecimal("12.22"));
    restTemplate.put(BASE_URL + url, item);
  }

  private void testCreateOrderItem_ResourceAlreadyExistsException(String url)
      throws JsonParseException, JsonMappingException, IOException {
    OrderItemDto item = new OrderItemDto(1L, 5L, new ProductDto(1L), 1L, new BigDecimal("10.22"));
    try {
      restTemplate.postForEntity(BASE_URL + url, item, OrderItemDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected CONFLICT status", HttpStatus.CONFLICT, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E001", error.getCode());
      assertEquals("Expected the same message", "Order Item found for id=1", error.getMessage());
    }
  }

  private void testCreateOrderItem(String url) {
    OrderItemDto item = new OrderItemDto(null, 5L, new ProductDto(1L), 1L, new BigDecimal("10.22"));
    ResponseEntity<OrderItemDto> itemSaved = restTemplate.postForEntity(BASE_URL + url, item, OrderItemDto.class);
    assertEquals("Expected CREATED status", HttpStatus.CREATED, itemSaved.getStatusCode());
    assertNotNull("Expected an item id", itemSaved.getBody().getId());
  }

  private void testGetOrderItems(String url) {
    ProductCategoryDto category = new ProductCategoryDto(6L, "Cameras & Optics", new Float("0.2"));
    ProductDto product = new ProductDto(1L, null, category, "lacinia. Sed congue, elit sed", new BigDecimal("9.458"),
        null, null,
        "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Curabitur sed tortor. Integer aliquam adipiscing lacus. Ut nec");
    OrderItemDto item = new OrderItemDto(1L, 1L, product, 1L, new BigDecimal("9.458"));
    ResponseEntity<OrderItemDto[]> items = restTemplate.getForEntity(BASE_URL + url, OrderItemDto[].class);
    assertEquals("Expected OK status", HttpStatus.OK, items.getStatusCode());
    assertEquals("Expected one item", 5, items.getBody().length);
    assertEquals("Expected one item", item, items.getBody()[0]);
  }

  private void testDeleteOrder_ResourceNotFoundException(String url)
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.delete(BASE_URL + url);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected Not Found status", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "No Order found for id=999", error.getMessage());
    }
  }

  private void testDeleteOrder_OrderWithInvoicesException(String url)
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.delete(BASE_URL + url);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected Not Found status", HttpStatus.CONFLICT, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E007", error.getCode());
      assertEquals("Expected the same message", "Impossible to delete invoice id=1 because it has invoices created",
          error.getMessage());
    }
  }

  private void testDeleteOrder(String url, String id) throws JsonParseException, JsonMappingException, IOException {
    restTemplate.delete(BASE_URL + url);
    try {
      restTemplate.getForEntity(BASE_URL + url, OrderDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected Not Found status", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "No Order found for id=" + id, error.getMessage());
    }
  }

  private void testUpdateOrder_ResourceNotFoundException(String url)
      throws JsonParseException, JsonMappingException, IOException {
    OrderDto order = new OrderDto(9999L, 2L, new Date(), new BigDecimal("10.22"));
    try {
      restTemplate.put(BASE_URL + url, order);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected Not Found status", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "No Order found for id=9999", error.getMessage());
    }
  }

  private void testUpdateOrder(String url) {
    OrderDto order = new OrderDto(2L, 2L, new Date(), new BigDecimal("10.22"));
    OrderItemDto item = new OrderItemDto(6L, 2L, new ProductDto(6L), 1L, new BigDecimal("10.22"));
    OrderItemDto item2 = new OrderItemDto(7L, 2L, new ProductDto(7L), 1L, new BigDecimal("10.22"));
    OrderItemDto item3 = new OrderItemDto(8L, 2L, new ProductDto(8L), 1L, new BigDecimal("10.22"));
    OrderItemDto item4 = new OrderItemDto(9L, 2L, new ProductDto(9L), 4L, new BigDecimal("10.22"));
    order.setItems(Arrays.asList(item, item2, item3, item4));
    restTemplate.put(BASE_URL + url, order);

    ResponseEntity<OrderDto> updatedOrder = restTemplate.getForEntity(BASE_URL + url, OrderDto.class);
    assertEquals("Expected created status", HttpStatus.OK, updatedOrder.getStatusCode());
    assertEquals("Expeted the correct total cost", new BigDecimal("65.694"), updatedOrder.getBody().getTotal());
    assertEquals("Expeted the same customer id", order.getCustomerId(), updatedOrder.getBody().getCustomerId());
    assertEquals("Expeted the same placed date", order.getPlaced(), updatedOrder.getBody().getPlaced());
    assertNull("Expected an empty list", updatedOrder.getBody().getItems());
  }

  private void testGetOrder_ResourceNotFoundException(String url)
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.getForEntity(BASE_URL + url, OrderDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected Not Found status", HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "No Order found for id=9999", error.getMessage());
    }
  }

  private void testGetOrder(String url) {
    Date opened = Date.from(LocalDate.of(2016, 6, 21).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    OrderDto expectedOrder = new OrderDto(1L, 1L, opened, new BigDecimal("181.088"));
    ResponseEntity<OrderDto> order = restTemplate.getForEntity(BASE_URL + url, OrderDto.class);
    assertEquals("Expected OK status", HttpStatus.OK, order.getStatusCode());
    assertEquals("Expected the same order", expectedOrder, order.getBody());
  }

  private void testCreateOrder_ResourceAlreadyExistsException(String url)
      throws JsonParseException, JsonMappingException, IOException {
    OrderDto order = new OrderDto(1L, 2L, new Date(), new BigDecimal("10.22"));
    OrderItemDto item = new OrderItemDto(null, 1L, new ProductDto(1L), 1L, new BigDecimal("10.22"));
    order.setItems(Arrays.asList(item));
    try {
      restTemplate.postForEntity(BASE_URL + url, order, OrderDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals("Expected created status", HttpStatus.CONFLICT, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E001", error.getCode());
      assertEquals("Expected the same message", "Order found for id=1", error.getMessage());
    }
  }

  private void testCreateOrder(String url) {
    OrderDto order = new OrderDto(null, 2L, new Date(), new BigDecimal("10.22"));
    OrderItemDto item = new OrderItemDto(null, null, new ProductDto(1L), 1L, new BigDecimal("10.22"));
    order.setItems(Arrays.asList(item));

    ResponseEntity<OrderDto> savedOrder = restTemplate.postForEntity(BASE_URL + url, order, OrderDto.class);
    assertEquals("Expected created status", HttpStatus.CREATED, savedOrder.getStatusCode());
    assertNotNull("Expected an order id", savedOrder.getBody().getId());
    assertEquals("Expeted the correct total cost", new BigDecimal("9.458"), savedOrder.getBody().getTotal());
    assertEquals("Expeted the same customer id", order.getCustomerId(), savedOrder.getBody().getCustomerId());
    assertEquals("Expeted the same placed date", order.getPlaced(), savedOrder.getBody().getPlaced());
    assertNull("Expected an empty list", savedOrder.getBody().getItems());
  }

}
