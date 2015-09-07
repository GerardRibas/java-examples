/**
 * 
 */
package cat.grc.spring.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import cat.grc.spring.data.EntityManagerConfiguration;
import cat.grc.spring.data.dto.OrderDto;
import cat.grc.spring.data.dto.OrderItemDto;
import cat.grc.spring.data.dto.ProductDto;
import cat.grc.spring.data.entity.Customer;
import cat.grc.spring.data.entity.Invoice;
import cat.grc.spring.data.entity.Order;
import cat.grc.spring.data.entity.OrderItem;
import cat.grc.spring.data.entity.Product;
import cat.grc.spring.data.entity.ProductCategory;
import cat.grc.spring.data.exception.OrderWithInvoicesException;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.repository.OrderItemRepository;
import cat.grc.spring.data.repository.OrderRepository;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class OrderServiceTest {

  private OrderServiceImpl service;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private ProductServicePkg productService;

  @Mock
  private OrderItemRepository orderItemRepository;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    service = new OrderServiceImpl();
    service.setOrderRepository(orderRepository);
    service.setModelMapper(new EntityManagerConfiguration().modelMapper());
    service.setProductService(productService);
    service.setOrderItemRepository(orderItemRepository);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindAllOrdersByCustomerId() {
    Long customerId = 1L;
    int page = 1;
    int size = 1;

    Pageable pageable = new PageRequest(page, size);
    Page<Order> orderPage = mock(Page.class);
    Order result = new Order(1L, new Customer(1L), new Date(), new BigDecimal("12.33"));
    when(orderRepository.findByCustomer(eq(customerId), eq(pageable))).thenReturn(orderPage);
    when(orderPage.getContent()).thenReturn(Arrays.asList(result));
    Collection<OrderDto> orders = service.findOrdersByCustomerId(customerId, page, size);
    assertFalse("Expected a non empty collection", orders.isEmpty());
    assertEquals("Expected an order", 1, orders.size());

    verify(orderRepository).findByCustomer(eq(customerId), eq(pageable));
    verifyNoMoreInteractions(orderRepository);
  }

  @Test
  public void testFindOrderById() {
    Long orderId = 1L;
    Order result = mock(Order.class);
    when(result.getCustomer()).thenReturn(mock(Customer.class));
    when(orderRepository.findOne(eq(orderId))).thenReturn(result);
    OrderDto order = service.findOrderById(orderId);
    assertNotNull("Expected an order", order);

    verify(orderRepository).findOne(eq(orderId));
    verifyNoMoreInteractions(orderRepository);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testFindOrderById_ResourceAlreadyExistsException() {
    Long orderId = 1L;
    Order result = mock(Order.class);
    when(result.getCustomer()).thenReturn(null);
    service.findOrderById(orderId);
  }


  @Test
  public void testAddNewOrder() {
    OrderDto order = new OrderDto(null, 1L, new Date(), new BigDecimal("11.330"));
    ProductDto product = new ProductDto(1L, null, null, "Some Cool Product", new BigDecimal("11.330"), "blue", "10x10",
        "Some cool product description");
    OrderItemDto item = new OrderItemDto(null, null, product, 1L, new BigDecimal("11.330"));
    order.setItems(Arrays.asList(item));


    Order entity = new Order(null, new Customer(1L), order.getPlaced(), order.getTotal());
    Product productEntity = new Product(product.getId(), null, null, product.getName(), product.getPrice(),
        product.getColor(), product.getSize(), product.getDescription());
    OrderItem itemEntity = new OrderItem(null, null, productEntity, item.getQuantity(), item.getCost());
    entity.setItems(new HashSet<>(Arrays.asList(itemEntity)));

    Order savedEntity = new Order(1L, new Customer(1L), order.getPlaced(), order.getTotal());
    OrderItem savedItemEntity =
        new OrderItem(1L, entity, new Product(item.getProduct().getId()), item.getQuantity(), item.getCost());
    savedEntity.setItems(new HashSet<>(Arrays.asList(savedItemEntity)));

    OrderDto expectedOrderDto = new OrderDto(savedEntity.getId(), savedEntity.getCustomer().getId(),
        savedEntity.getPlaced(), savedEntity.getTotal());
    when(productService.findProductEntityById(eq(item.getProduct().getId()))).thenReturn(productEntity);
    when(orderRepository.save(eq(entity))).thenReturn(savedEntity);

    OrderDto savedDto = service.addOrder(order);
    assertNotNull("Expected a saved order", savedDto);
    assertEquals("Expected the same order", expectedOrderDto, savedDto);
    assertNull("Expected no items on the returned object", savedDto.getItems());

    verify(orderRepository).save(eq(entity));
    verify(productService).findProductEntityById(eq(product.getId()));
    verifyNoMoreInteractions(orderRepository);
    verifyNoMoreInteractions(productService);
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testAddNewOrder_ResourceAlreadyExistsException() {
    OrderDto order = new OrderDto(1L, 1L, new Date(), new BigDecimal("11.33"));
    OrderItemDto item = new OrderItemDto(null, null, null, 1L, new BigDecimal("11.33"));
    order.setItems(Arrays.asList(item));
    when(orderRepository.exists(order.getId())).thenReturn(true);
    service.addOrder(order);
  }

  @Test
  public void testUpdateOrder() {
    OrderDto order = new OrderDto(1L, 1L, new Date(), new BigDecimal("11.330"));
    ProductDto product = new ProductDto(1L, null, null, "Some Cool Product", new BigDecimal("11.330"), "blue", "10x10",
        "Some cool product description");
    OrderItemDto item = new OrderItemDto(1L, order.getId(), product, 1L, new BigDecimal("11.330"));
    order.setItems(Arrays.asList(item));


    Order entity = new Order(order.getId(), new Customer(1L), order.getPlaced(), order.getTotal());
    Product productEntity = new Product(product.getId(), null, null, product.getName(), product.getPrice(),
        product.getColor(), product.getSize(), product.getDescription());
    OrderItem itemEntity = new OrderItem(item.getId(), entity, productEntity, item.getQuantity(), item.getCost());
    entity.setItems(new HashSet<>(Arrays.asList(itemEntity)));

    OrderDto expectedOrderDto =
        new OrderDto(entity.getId(), entity.getCustomer().getId(), entity.getPlaced(), entity.getTotal());
    when(productService.findProductById(eq(item.getProduct().getId()))).thenReturn(product);
    when(orderRepository.exists(eq(order.getId()))).thenReturn(true);
    when(orderRepository.save(eq(entity))).thenReturn(entity);
    when(productService.findProductEntityById(eq(product.getId()))).thenReturn(productEntity);

    OrderDto updatedDto = service.updateOrder(order);
    assertNotNull("Expected an updated order", updatedDto);
    assertEquals("Expected the same order", expectedOrderDto, updatedDto);
    assertNull("Expected no items on the returned object", updatedDto.getItems());

    verify(orderRepository).exists(eq(order.getId()));
    verify(orderRepository).save(eq(entity));
    verify(productService).findProductEntityById(eq(product.getId()));
    verifyNoMoreInteractions(orderRepository);
    verifyNoMoreInteractions(productService);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testUpdateOrder_ResourceNotFoundException() {
    OrderDto order = new OrderDto(1L, 1L, new Date(), new BigDecimal("11.33"));
    ProductDto product = new ProductDto(1L, null, null, "Some Cool Product", new BigDecimal("11.33"), "blue", "10x10",
        "Some cool product description");
    OrderItemDto item = new OrderItemDto(1L, order.getId(), product, 1L, new BigDecimal("11.33"));
    order.setItems(Arrays.asList(item));
    when(orderRepository.exists(eq(order.getId()))).thenReturn(false);
    service.updateOrder(order);
  }

  @Test
  public void testDeleteOrder() {
    Long orderId = 1L;
    Order order = new Order();
    order.setId(orderId);
    order.setCustomer(new Customer(1L));
    order.setInvoices(Collections.emptyList());
    when(orderRepository.findOne(eq(orderId))).thenReturn(order);
    service.deleteOrder(orderId);

    verify(orderRepository).findOne(eq(orderId));
    verify(orderRepository).delete(eq(orderId));
    verifyZeroInteractions(productService);
    verifyNoMoreInteractions(orderRepository);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testDeleteOrder_ResourceNotFoundException() {
    Long orderId = 1L;
    when(orderRepository.findOne(eq(orderId))).thenReturn(null);
    service.deleteOrder(orderId);
  }

  @Test(expected = OrderWithInvoicesException.class)
  public void testDeleteOrder_OrderWithInvoicesException() {
    Long orderId = 1L;
    Order order = new Order();
    order.setId(orderId);
    order.setCustomer(new Customer(1L));

    Invoice invoice = new Invoice();
    invoice.setOrder(order);
    order.setInvoices(Arrays.asList(invoice));

    when(orderRepository.findOne(eq(orderId))).thenReturn(order);
    service.deleteOrder(orderId);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindItemsByOrderId() {
    Long orderId = 1L;
    int page = 1;
    int size = 15;
    Pageable pageable = new PageRequest(page, size);
    Page<OrderItem> itemsPage = mock(Page.class);
    OrderItem orderItem = new OrderItem();
    when(orderItemRepository.findByOrder(eq(orderId), eq(pageable))).thenReturn(itemsPage);
    when(itemsPage.getContent()).thenReturn(Arrays.asList(orderItem));
    Collection<OrderItemDto> items = service.findItemsByOrderId(orderId, page, size);
    assertFalse("Expected a collection with an item on it", items.isEmpty());
    verify(orderItemRepository).findByOrder(eq(orderId), eq(pageable));
  }

  @Test
  public void testAddOrderItem() {
    OrderItemDto newItem = new OrderItemDto(null, 1L, new ProductDto(1L), 1L, new BigDecimal("10.000"));

    Product product = new Product(1L, null, new ProductCategory(1L, "Pets", new Float("0.2")), "Some Name",
        new BigDecimal("10.000"), "Blue", "10x10", "Some Desc");
    Order orderToSave = new Order(1L, new Customer(1L), new Date(), new BigDecimal(("10.000")));
    Set<OrderItem> itemsToSave = new HashSet<>();
    itemsToSave.add(new OrderItem(null, orderToSave, product, 1L, newItem.getCost()));
    orderToSave.setItems(itemsToSave);

    Order orderSaved = new Order(1L, new Customer(1L), new Date(), new BigDecimal(("20.000")));
    Set<OrderItem> itemsSaved = new HashSet<>();
    itemsSaved.add(new OrderItem(1L, orderSaved, product, 1L, newItem.getCost()));
    orderSaved.setItems(itemsSaved);

    when(orderRepository.findOne(eq(newItem.getOrderId()))).thenReturn(orderToSave);
    when(orderRepository.exists(eq(newItem.getOrderId()))).thenReturn(true);
    when(productService.findProductEntityById(eq(product.getId()))).thenReturn(product);
    when(orderRepository.save(eq(orderToSave))).thenReturn(orderSaved);

    OrderItemDto item = service.addOrderItem(newItem);
    assertNotNull("Expeted an item", item);
    assertEquals("Expeted an itemId created", 1L, item.getId().longValue());

    verify(orderRepository).findOne(eq(newItem.getOrderId()));
    verify(orderRepository).save(eq(orderToSave));
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testAddOrderItem_ResourceAlreadyExistsException() {
    OrderItemDto newItem = new OrderItemDto(1L, 1L, new ProductDto(1L), 1L, new BigDecimal("10.000"));
    when(orderItemRepository.exists(eq(newItem.getId()))).thenReturn(true);
    service.addOrderItem(newItem);
  }

  @Test
  public void testUpdateOrderItem() {
    OrderItemDto itemToUpdate = new OrderItemDto(1L, 1L, new ProductDto(1L), 2L, new BigDecimal("20.000"));

    Order order = new Order(1L, new Customer(1L), new Date(), new BigDecimal(("10.000")));
    Set<OrderItem> items = new HashSet<>();
    Product product = new Product(1L, null, new ProductCategory(1L, "Pets", new Float("0.2")), "Some Name",
        new BigDecimal("10.000"), "Blue", "10x10", "Some Desc");
    items.add(new OrderItem(1L, order, product, 1L, new BigDecimal("10.000")));
    order.setItems(items);

    Order orderToSave = new Order(1L, new Customer(1L), order.getPlaced(), new BigDecimal(("20.000")));
    Set<OrderItem> itemsToSave = new HashSet<>();
    itemsToSave.add(new OrderItem(1L, orderToSave, product, 2L, new BigDecimal("20.000")));
    orderToSave.setItems(itemsToSave);

    when(productService.findProductEntityById(eq(product.getId()))).thenReturn(product);
    when(orderRepository.findOne(eq(itemToUpdate.getOrderId()))).thenReturn(order);
    when(orderRepository.exists(eq(itemToUpdate.getOrderId()))).thenReturn(true);
    when(orderItemRepository.exists(eq(itemToUpdate.getId()))).thenReturn(true);
    when(orderRepository.save(orderToSave)).thenReturn(orderToSave);
    OrderItemDto dtoUpdated = service.updateOrderItem(itemToUpdate);
    assertNotNull("Expected a returned order item", dtoUpdated);

    verify(productService).findProductEntityById(eq(product.getId()));
    verify(orderRepository).findOne(eq(itemToUpdate.getOrderId()));
    verify(orderRepository).save(eq(orderToSave));
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testUpdateOrderItem_ResourceNotFoundException() {
    OrderItemDto itemToUpdate = new OrderItemDto(1L, 1L, new ProductDto(1L), 2L, new BigDecimal("20.000"));
    when(orderRepository.findOne(eq(itemToUpdate.getId()))).thenReturn(mock(Order.class));
    service.updateOrderItem(itemToUpdate);
  }


  @Test
  public void testUpdateOrderItemMultipleItems() {
    OrderItemDto itemToUpdate = new OrderItemDto(1L, 1L, new ProductDto(1L), 2L, new BigDecimal("20.000"));

    Order order = new Order(1L, new Customer(1L), new Date(), new BigDecimal(("20.000")));
    Set<OrderItem> items = new HashSet<>();
    Product product = new Product(1L, null, new ProductCategory(1L, "Pets", new Float("0.2")), "Some Name",
        new BigDecimal("10.000"), "Blue", "10x10", "Some Desc");
    items.add(new OrderItem(1L, order, product, 1L, new BigDecimal("10.000")));
    items.add(new OrderItem(2L, order, product, 1L, new BigDecimal("10.000")));
    order.setItems(items);

    Order orderToSave = new Order(1L, new Customer(1L), order.getPlaced(), new BigDecimal(("30.000")));
    Set<OrderItem> itemsToSave = new HashSet<>();
    itemsToSave.add(new OrderItem(2L, orderToSave, product, 1L, new BigDecimal("10.000")));
    itemsToSave.add(new OrderItem(1L, orderToSave, product, 2L, new BigDecimal("20.000")));
    orderToSave.setItems(itemsToSave);

    when(productService.findProductEntityById(eq(product.getId()))).thenReturn(product);
    when(orderRepository.findOne(eq(itemToUpdate.getOrderId()))).thenReturn(order);
    when(orderRepository.exists(eq(itemToUpdate.getOrderId()))).thenReturn(true);
    when(orderItemRepository.exists(eq(itemToUpdate.getId()))).thenReturn(true);
    when(orderRepository.save(eq(orderToSave))).thenReturn(orderToSave);
    OrderItemDto dtoUpdated = service.updateOrderItem(itemToUpdate);
    assertNotNull("Expected a returned order item", dtoUpdated);

    verify(productService, new Times(2)).findProductEntityById(eq(product.getId()));
    verify(orderRepository).findOne(eq(itemToUpdate.getOrderId()));
    verify(orderRepository).save(eq(orderToSave));
  }

  @Test
  public void testDeleteOrderItem() {
    Long id = 1L;

    Order order = new Order(1L, new Customer(1L), new Date(), new BigDecimal(("20.000")));
    Set<OrderItem> items = new HashSet<>();
    Product product = new Product(1L, null, new ProductCategory(1L, "Pets", new Float("0.2")), "Some Name",
        new BigDecimal("10.000"), "Blue", "10x10", "Some Desc");
    OrderItem item = new OrderItem(1L, order, product, 1L, new BigDecimal("10.000"));
    items.add(new OrderItem(1L, order, product, 1L, new BigDecimal("10.000")));
    items.add(new OrderItem(2L, order, product, 1L, new BigDecimal("10.000")));
    order.setItems(items);

    Order orderToSave = new Order(1L, new Customer(1L), order.getPlaced(), new BigDecimal(("10.000")));
    Set<OrderItem> itemsToSave = new HashSet<>();
    itemsToSave.add(new OrderItem(2L, order, product, 1L, new BigDecimal("10.000")));
    orderToSave.setItems(itemsToSave);

    when(orderItemRepository.findOne(eq(id))).thenReturn(item);
    when(orderRepository.exists(eq(order.getId()))).thenReturn(true);
    when(orderRepository.findOne(eq(order.getId()))).thenReturn(order);
    when(productService.findProductEntityById(eq(product.getId()))).thenReturn(product);
    when(orderRepository.save(eq(orderToSave))).thenReturn(orderToSave);
    service.deleteOrderItem(id);

    verify(orderItemRepository).findOne(eq(id));
    verify(orderRepository).findOne(eq(order.getId()));
    verify(productService).findProductEntityById(eq(product.getId()));
    verify(orderRepository).save(eq(orderToSave));
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testDeleteOrderItem_ResourceNotFoundException() {
    Long id = 99999L;
    when(orderItemRepository.findOne(eq(id))).thenReturn(null);
    service.deleteOrderItem(id);
  }

}

