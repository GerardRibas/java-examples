/**
 * 
 */
package cat.grc.spring.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
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
import cat.grc.spring.data.dto.OrderDto;
import cat.grc.spring.data.dto.OrderItemDto;
import cat.grc.spring.data.dto.ProductDto;
import cat.grc.spring.data.exception.OrderWithInvoicesException;
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
@DatabaseSetup("OrderServiceTestIT_setup.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderServiceTestIT {

  @Autowired
  private OrderService service;

  @Autowired
  private EntityManager entityManager;

  @Test
  public void testFindOrdersByCustomerId() {
    Collection<OrderDto> orders = service.findOrdersByCustomerId(1L, 0, 15);
    assertFalse("At least one order for this customerId", orders.isEmpty());
    orders.forEach(order -> assertEquals("Expected the same customerId", 1L, order.getCustomerId().longValue()));
  }

  @Test
  public void testFindOrderById() {
    OrderDto order = service.findOrderById(1L);
    assertNotNull("Expected an order found", order);
    assertEquals("Expected the sameOrderId", 1L, order.getId().longValue());
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testFindOrderById_ResourceNotFoundException() {
    service.findOrderById(99999L);
  }

  @Test
  @ExpectedDatabase(value = "OrderServiceTestIT.testAddOrder.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testAddOrder() {
    Date placed = Date.from(LocalDate.of(2015, 8, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    OrderDto order = new OrderDto(null, 2L, placed, new BigDecimal("10.00"));
    order.setItems(Arrays.asList(new OrderItemDto(null, null, new ProductDto(1L), 1L, new BigDecimal("10"))));
    OrderDto savedOrder = service.addOrder(order);
    assertNotNull("Expeted a saved order returned", savedOrder);
    assertNotNull("Expeted an order id", savedOrder.getId());
    assertEquals("Expeted corrected total price", new BigDecimal("9.458"), savedOrder.getTotal());
    entityManager.flush();
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testAddOrder_ResourceAlreadyExistsException() {
    Date placed = Date.from(LocalDate.of(2015, 8, 31).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    OrderDto order = new OrderDto(1L, 2L, placed, new BigDecimal("10.00"));
    order.setItems(Arrays.asList(new OrderItemDto(null, null, new ProductDto(1L), 1L, new BigDecimal("10"))));
    service.addOrder(order);
  }

  @Test
  @ExpectedDatabase(value = "OrderServiceTestIT.testUpdateOrder_AddProductOnIt.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testUpdateOrder_AddProductOnIt() {
    Date placed = Date.from(LocalDate.of(2015, 8, 30).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    OrderDto order = new OrderDto(1L, 1L, placed, new BigDecimal("9.458"));
    order.setItems(Arrays.asList(new OrderItemDto(1L, 1L, new ProductDto(1L), 1L, new BigDecimal("9.458")),
        new OrderItemDto(2L, 1L, new ProductDto(2L), 1L, new BigDecimal("10.000")),
        new OrderItemDto(null, 1L, new ProductDto(2L), 1L, new BigDecimal("10.000"))));
    OrderDto savedOrder = service.updateOrder(order);
    assertNotNull("Expeted a saved order returned", savedOrder);
    entityManager.flush();
  }

  @Test
  @ExpectedDatabase(value = "OrderServiceTestIT.testUpdateOrder_UpdateProductOnIt.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testUpdateOrder_UpdateProductOnIt() {
    Date placed = Date.from(LocalDate.of(2015, 8, 30).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    OrderDto order = new OrderDto(1L, 1L, placed, new BigDecimal("18.916"));
    order.setItems(Arrays.asList(new OrderItemDto(1L, 1L, new ProductDto(1L), 2L, new BigDecimal("9.458")),
        new OrderItemDto(2L, 1L, new ProductDto(2L), 1L, new BigDecimal("10.000"))));
    OrderDto savedOrder = service.updateOrder(order);
    assertNotNull("Expeted a saved order returned", savedOrder);
    entityManager.flush();
  }

  @Test
  @ExpectedDatabase(value = "OrderServiceTestIT.testUpdateOrder_DeleteProductOnIt.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testUpdateOrder_DeleteProductOnIt() {
    Date placed = Date.from(LocalDate.of(2015, 8, 30).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    OrderDto order = new OrderDto(1L, 1L, placed, new BigDecimal("18.916"));
    order.setItems(Arrays.asList(new OrderItemDto(1L, 1L, new ProductDto(1L), 1L, new BigDecimal("9.458"))));
    OrderDto savedOrder = service.updateOrder(order);
    assertNotNull("Expeted a saved order returned", savedOrder);
    entityManager.flush();
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testUpdateOrder_ResourceNotFoundException() {
    Date placed = Date.from(LocalDate.of(2015, 8, 30).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    OrderDto order = new OrderDto(99999L, 1L, placed, new BigDecimal("18.916"));
    order.setItems(Arrays.asList(new OrderItemDto(1L, 1L, new ProductDto(1L), 1L, new BigDecimal("9.458"))));
    service.updateOrder(order);
  }

  @Test
  @ExpectedDatabase(value = "OrderServiceTestIT.testDeleteOrder.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testDeleteOrder() {
    service.deleteOrder(1L);
    entityManager.flush();
  }

  @Test(expected = OrderWithInvoicesException.class)
  public void testDeleteOrder_OrderWithInvoicesException() {
    service.deleteOrder(2L);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testDeleteOrder_ResourceNotFoundException() {
    service.deleteOrder(99999L);
  }

  @Test
  public void testFindItemsByOrderId() {
    Collection<OrderItemDto> items = service.findItemsByOrderId(1L, 0, 15);
    assertFalse("Expected items for this order", items.isEmpty());
  }

  @Test
  @ExpectedDatabase(value = "OrderServiceTestIT.testAddOrderItem.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testAddOrderItem() {
    OrderItemDto newItem = new OrderItemDto(null, 1L, new ProductDto(2L), 1L, new BigDecimal("10.000"));
    OrderItemDto savedItem = service.addOrderItem(newItem);
    entityManager.flush();
    assertNotNull("Expected a saved item", savedItem);
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testAddOrderItem_ResourceAlreadyExistsException() {
    OrderItemDto newItem = new OrderItemDto(1L, 1L, new ProductDto(2L), 1L, new BigDecimal("10.000"));
    service.addOrderItem(newItem);
  }

  @Test
  @ExpectedDatabase(value = "OrderServiceTestIT.testUpdateOrderItem.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testUpdateOrderItem() {
    OrderItemDto item = new OrderItemDto(1L, 1L, new ProductDto(2L), 1L, new BigDecimal("10.000"));
    OrderItemDto itemUpdated = service.updateOrderItem(item);
    entityManager.flush();
    assertEquals("Expected the same item id returned", item.getId(), itemUpdated.getId());
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testUpdateOrderItem_ResourceNotFoundException() {
    OrderItemDto item = new OrderItemDto(99999L, 1L, new ProductDto(2L), 1L, new BigDecimal("10.000"));
    service.updateOrderItem(item);
  }

  @Test
  @ExpectedDatabase(value = "OrderServiceTestIT.testDeleteOrderItem.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testDeleteOrderItem() {
    service.deleteOrderItem(1L);
    entityManager.flush();
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testDeleteOrderItem_() {
    service.deleteOrderItem(9999999L);
  }

}
