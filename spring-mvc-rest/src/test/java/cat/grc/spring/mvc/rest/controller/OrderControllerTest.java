/**
 * 
 */
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cat.grc.spring.data.dto.OrderDto;
import cat.grc.spring.data.dto.OrderItemDto;
import cat.grc.spring.data.dto.ProductDto;
import cat.grc.spring.data.exception.OrderWithInvoicesException;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.service.OrderService;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class OrderControllerTest {

  private MockMvc mvc;

  private ObjectMapper objectMapper;

  @Mock
  private OrderService service;

  @Before
  public void before() {
    objectMapper = new ObjectMapper();
    MockitoAnnotations.initMocks(this);
    OrderController controller = new OrderController();
    controller.setService(service);
    mvc = standaloneSetup(controller).setHandlerExceptionResolvers(createExceptionResolver()).build();
  }

  @Test
  public void testGetOrdersOfACustomer() throws Exception {
    OrderDto order = new OrderDto(1L, 1L, new Date(), new BigDecimal("11.22"));
    when(service.findOrdersByCustomerId(eq(order.getCustomerId()), eq(0), eq(15))).thenReturn(Arrays.asList(order));
    mvc.perform(get("/customers/1/orders").accept(APPLICATION_JSON_UTF8)).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].customerId", is(1)))
        .andExpect(jsonPath("$[0].placed", is(order.getPlaced().getTime())))
        .andExpect(jsonPath("$[0].total", is(11.22)));

    verify(service).findOrdersByCustomerId(eq(order.getCustomerId()), eq(0), eq(15));
  }

  @Test
  public void testCreateOrderPathCustomers() throws Exception {
    testCreateOrder("/customers/1/orders");
  }

  @Test
  public void testCreateOrder() throws Exception {
    testCreateOrder("/orders");
  }

  @Test
  public void testCreateOrder_ResourceAlreadyExistsException() throws JsonProcessingException, Exception {
    testCreateOrder_ResourceAlreadyExistsException("/orders");
  }

  @Test
  public void testCreateOrderPathCustomers_ResourceAlreadyExistsException() throws JsonProcessingException, Exception {
    testCreateOrder_ResourceAlreadyExistsException("/customers/1/orders");
  }

  @Test
  public void testGetOrder() throws Exception {
    testGetOrder("/orders/1");
  }

  @Test
  public void testGetOrderPathCustomers() throws Exception {
    testGetOrder("/customers/1/orders/1");
  }

  @Test
  public void testGetOrder_ResourceNotFoundExceotion() throws Exception {
    testGetOrder_ResourceNotFoundExceotion("/orders/1");
  }

  @Test
  public void testGetOrderPathCustomers_ResourceNotFoundExceotion() throws Exception {
    testGetOrder_ResourceNotFoundExceotion("/customers/1/orders/1");
  }

  @Test
  public void testUpdateOrder() throws JsonProcessingException, Exception {
    testUpdateOrder("/orders/1");
  }

  @Test
  public void testUpdateOrderCustomersPath() throws JsonProcessingException, Exception {
    testUpdateOrder("/customers/1/orders/1");
  }

  @Test
  public void testUpdateOrder_ResourceNotFoundException() throws JsonProcessingException, Exception {
    testUpdateOrder_ResourceNotFoundException("/orders/1");
  }

  @Test
  public void testUpdateOrderCustomersPath_ResourceNotFoundException() throws JsonProcessingException, Exception {
    testUpdateOrder_ResourceNotFoundException("/customers/1/orders/1");
  }

  @Test
  public void testDeleteOrder() throws JsonProcessingException, Exception {
    testDeleteOrder("/orders/1");
  }

  @Test
  public void testDeleteOrderCustomersPath() throws JsonProcessingException, Exception {
    testDeleteOrder("/customers/1/orders/1");
  }

  @Test
  public void testDeleteOrder_ResourceNotFoundException() throws JsonProcessingException, Exception {
    testDeleteOrder_ResourceNotFoundException("/orders/1");
  }

  @Test
  public void testDeleteOrderCustomersPath_ResourceNotFoundException() throws JsonProcessingException, Exception {
    testDeleteOrder_ResourceNotFoundException("/customers/1/orders/1");
  }

  @Test
  public void testDeleteOrder_OrderWithInvoicesException() throws JsonProcessingException, Exception {
    testDeleteOrder_OrderWithInvoicesException("/orders/1");
  }

  @Test
  public void testDeleteOrderCustomersPath_OrderWithInvoicesException() throws JsonProcessingException, Exception {
    testDeleteOrder_OrderWithInvoicesException("/customers/1/orders/1");
  }

  @Test
  public void testGetOrderItems() throws JsonProcessingException, Exception {
    testGetOrderItems("/orders/1/items");
  }

  @Test
  public void testGetOrderItemsCustomersPath() throws JsonProcessingException, Exception {
    testGetOrderItems("/customers/1/orders/1/items");
  }

  @Test
  public void testCreateOrderItem() throws JsonProcessingException, Exception {
    testCreateOrderItem("/orders/1/items");
  }

  @Test
  public void testCreateOrderItemCustomersPath() throws JsonProcessingException, Exception {
    testCreateOrderItem("/customers/1/orders/1/items");
  }

  @Test
  public void testCreateOrderItem_ResourceAlreadyExistsException() throws JsonProcessingException, Exception {
    testCreateOrderItem_ResourceAlreadyExistsException("/orders/1/items");
  }

  @Test
  public void testCreateOrderItemCustomersPath_ResourceAlreadyExistsException()
      throws JsonProcessingException, Exception {
    testCreateOrderItem_ResourceAlreadyExistsException("/customers/1/orders/1/items");
  }

  @Test
  public void testUpdateOrderItem() throws JsonProcessingException, Exception {
    testUpdateOrderItem("/orders/1/items/1");
  }

  @Test
  public void testUpdateOrderItemCustomersPath() throws JsonProcessingException, Exception {
    testUpdateOrderItem("/customers/1/orders/1/items/1");
  }

  @Test
  public void testUpdateOrderItem_ResourceNotFoundException() throws JsonProcessingException, Exception {
    testUpdateOrderItem_ResourceNotFoundException("/orders/1/items/1");
  }

  @Test
  public void testUpdateOrderItemCustomersPath_ResourceNotFoundException() throws JsonProcessingException, Exception {
    testUpdateOrderItem_ResourceNotFoundException("/customers/1/orders/1/items/1");
  }

  @Test
  public void testDeleteOrderItem() throws JsonProcessingException, Exception {
    testDeleteOrderItem("/orders/1/items/1");
  }

  @Test
  public void testDeleteOrderItemCustomersPath() throws JsonProcessingException, Exception {
    testDeleteOrderItem("/customers/1/orders/1/items/1");
  }

  @Test
  public void testDeleteOrderItem_ResourceNotFoundException() throws JsonProcessingException, Exception {
    testDeleteOrderItem_ResourceNotFoundException("/orders/1/items/1");
  }

  @Test
  public void testDeleteOrderItemCustomersPath_ResourceNotFoundException() throws JsonProcessingException, Exception {
    testDeleteOrderItem_ResourceNotFoundException("/customers/1/orders/1/items/1");
  }

  private void testDeleteOrderItem_ResourceNotFoundException(String url) throws Exception {
    doThrow(ResourceNotFoundException.class).when(service).deleteOrderItem(eq(1L));
    mvc.perform(delete(url)).andExpect(status().isNotFound()).andExpect(jsonPath("$.code", is("E002")));
    verify(service).deleteOrderItem(eq(1L));
  }

  private void testDeleteOrderItem(String url) throws Exception {
    mvc.perform(delete(url)).andExpect(status().isNoContent());
    verify(service).deleteOrderItem(eq(1L));
  }

  @SuppressWarnings("unchecked")
  private void testUpdateOrderItem_ResourceNotFoundException(String url) throws JsonProcessingException, Exception {
    ProductDto product = new ProductDto(1L);
    OrderItemDto item = new OrderItemDto(1L, 1L, product, 1L, new BigDecimal("11.22"));
    when(service.updateOrderItem(eq(item))).thenThrow(ResourceNotFoundException.class);
    mvc.perform(put(url).accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(item))).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("E002")));
    verify(service).updateOrderItem(eq(item));
  }

  private void testUpdateOrderItem(String url) throws JsonProcessingException, Exception {
    ProductDto product = new ProductDto(1L);
    OrderItemDto item = new OrderItemDto(1L, 1L, product, 1L, new BigDecimal("11.22"));
    when(service.updateOrderItem(eq(item))).thenReturn(item);
    mvc.perform(put(url).accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(item))).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.orderId", is(1))).andExpect(jsonPath("$.product.id", is(1)))
        .andExpect(jsonPath("$.cost", is(11.22)));
    verify(service).updateOrderItem(eq(item));
  }

  @SuppressWarnings("unchecked")
  private void testCreateOrderItem_ResourceAlreadyExistsException(String url)
      throws JsonProcessingException, Exception {
    ProductDto product = new ProductDto(1L);
    OrderItemDto item = new OrderItemDto(null, 1L, product, 1L, new BigDecimal("11.22"));
    when(service.addOrderItem(eq(item))).thenThrow(ResourceAlreadyExistsException.class);
    mvc.perform(post(url).accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(item))).andExpect(status().isConflict())
        .andExpect(jsonPath("$.code", is("E001")));
    verify(service).addOrderItem(eq(item));
  }

  private void testCreateOrderItem(String url) throws JsonProcessingException, Exception {
    ProductDto product = new ProductDto(1L);
    OrderItemDto item = new OrderItemDto(null, 1L, product, 1L, new BigDecimal("11.22"));
    OrderItemDto savedItem = new OrderItemDto(1L, 1L, product, 1L, new BigDecimal("11.22"));
    when(service.addOrderItem(eq(item))).thenReturn(savedItem);
    mvc.perform(post(url).accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(item))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1))).andExpect(jsonPath("$.orderId", is(1)))
        .andExpect(jsonPath("$.product.id", is(1))).andExpect(jsonPath("$.cost", is(11.22)));
    verify(service).addOrderItem(eq(item));
  }

  private void testGetOrderItems(String url) throws Exception {
    ProductDto product = new ProductDto(1L);
    OrderItemDto item = new OrderItemDto(1L, 1L, product, 1L, new BigDecimal("11.22"));
    when(service.findItemsByOrderId(eq(item.getOrderId()), eq(0), eq(15))).thenReturn(Arrays.asList(item));
    mvc.perform(get(url).accept(APPLICATION_JSON_UTF8)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(1))).andExpect(jsonPath("$[0].orderId", is(1)))
        .andExpect(jsonPath("$[0].product.id", is(1))).andExpect(jsonPath("$[0].cost", is(11.22)));
    verify(service).findItemsByOrderId(eq(item.getOrderId()), eq(0), eq(15));
  }

  private void testDeleteOrder_OrderWithInvoicesException(String url) throws Exception {
    doThrow(OrderWithInvoicesException.class).when(service).deleteOrder(eq(1L));
    mvc.perform(delete(url)).andExpect(status().isConflict()).andExpect(jsonPath("$.code", is("E007")));
    verify(service).deleteOrder(eq(1L));
  }

  private void testDeleteOrder_ResourceNotFoundException(String url) throws Exception {
    doThrow(ResourceNotFoundException.class).when(service).deleteOrder(eq(1L));
    mvc.perform(delete(url)).andExpect(status().isNotFound()).andExpect(jsonPath("$.code", is("E002")));
    verify(service).deleteOrder(eq(1L));
  }

  private void testDeleteOrder(String url) throws Exception {
    mvc.perform(delete(url)).andExpect(status().isNoContent());
    verify(service).deleteOrder(eq(1L));
  }

  @SuppressWarnings("unchecked")
  private void testUpdateOrder_ResourceNotFoundException(String url) throws JsonProcessingException, Exception {
    OrderDto order = new OrderDto(1L, 1L, new Date(), new BigDecimal("11.22"));
    when(service.updateOrder(eq(order))).thenThrow(ResourceNotFoundException.class);
    mvc.perform(put(url).accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(order))).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("E002")));
    verify(service).updateOrder(eq(order));
  }

  private void testUpdateOrder(String url) throws JsonProcessingException, Exception {
    OrderDto order = new OrderDto(1L, 1L, new Date(), new BigDecimal("11.22"));
    when(service.updateOrder(eq(order))).thenReturn(order);
    mvc.perform(put(url).accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(order))).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.customerId", is(1))).andExpect(jsonPath("$.placed", is(order.getPlaced().getTime())))
        .andExpect(jsonPath("$.total", is(11.22)));
    verify(service).updateOrder(eq(order));
  }

  @SuppressWarnings("unchecked")
  private void testGetOrder_ResourceNotFoundExceotion(String url) throws Exception {
    when(service.findOrderById(eq(1L))).thenThrow(ResourceNotFoundException.class);
    mvc.perform(get(url).accept(APPLICATION_JSON_UTF8)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("E002")));
    verify(service).findOrderById(eq(1L));
  }

  private void testGetOrder(String url) throws Exception {
    OrderDto order = new OrderDto(1L, 1L, new Date(), new BigDecimal("11.22"));
    when(service.findOrderById(eq(order.getId()))).thenReturn(order);
    mvc.perform(get(url).accept(APPLICATION_JSON_UTF8)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.customerId", is(1))).andExpect(jsonPath("$.placed", is(order.getPlaced().getTime())))
        .andExpect(jsonPath("$.total", is(11.22)));

    verify(service).findOrderById(eq(order.getId()));
  }

  @SuppressWarnings("unchecked")
  private void testCreateOrder_ResourceAlreadyExistsException(String url) throws JsonProcessingException, Exception {
    OrderDto order = new OrderDto(1L, 1L, new Date(), new BigDecimal("11.22"));
    when(service.addOrder(eq(order))).thenThrow(ResourceAlreadyExistsException.class);
    mvc.perform(post(url).accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(order))).andExpect(status().isConflict())
        .andExpect(jsonPath("$.code", is("E001")));
    verify(service).addOrder(eq(order));
  }

  private void testCreateOrder(String url) throws JsonProcessingException, Exception {
    OrderDto order = new OrderDto(null, 1L, new Date(), new BigDecimal("11.22"));
    OrderDto savedOrder = new OrderDto(1L, 1L, order.getPlaced(), new BigDecimal("11.22"));
    when(service.addOrder(eq(order))).thenReturn(savedOrder);
    mvc.perform(post(url).accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(order))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1))).andExpect(jsonPath("$.customerId", is(1)))
        .andExpect(jsonPath("$.placed", is(order.getPlaced().getTime()))).andExpect(jsonPath("$.total", is(11.22)));
    verify(service).addOrder(eq(order));
  }

}
