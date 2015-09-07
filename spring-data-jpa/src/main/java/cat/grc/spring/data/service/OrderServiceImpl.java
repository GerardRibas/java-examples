/**
 * 
 */
package cat.grc.spring.data.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import cat.grc.spring.data.dto.OrderDto;
import cat.grc.spring.data.dto.OrderItemDto;
import cat.grc.spring.data.entity.Order;
import cat.grc.spring.data.entity.OrderItem;
import cat.grc.spring.data.entity.Product;
import cat.grc.spring.data.exception.OrderWithInvoicesException;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.repository.OrderItemRepository;
import cat.grc.spring.data.repository.OrderRepository;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@Service
public class OrderServiceImpl implements OrderService, OrderServicePkg {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

  private static final String NO_ORDER_FOUND_MSG = "No Order found for id=%d";

  private OrderRepository orderRepository;

  private OrderItemRepository orderItemRepository;

  private ModelMapper modelMapper;

  private ProductServicePkg productService;

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.OrderService#findOrdersByCustomerId(java.lang.Long, int, int)
   */
  @Override
  @Transactional(readOnly = true)
  public Collection<OrderDto> findOrdersByCustomerId(Long customerId, int page, int size) {
    LOGGER.debug("Finding orders by customerId={} page={} and size={}", customerId, page, size);
    Assert.notNull(customerId);
    Pageable pageable = new PageRequest(page, size);
    Page<Order> ordersPage = orderRepository.findByCustomer(customerId, pageable);
    return ordersPage.getContent().stream().map(order -> modelMapper.map(order, OrderDto.class))
        .collect(Collectors.toList());
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.OrderService#findOrderById(java.lang.Long)
   */
  @Override
  @Transactional(readOnly = true)
  public OrderDto findOrderById(Long id) {
    return modelMapper.map(findOrder(id), OrderDto.class);
  }

  @Override
  @Transactional(readOnly = true)
  public Order findOrder(Long id) {
    LOGGER.debug("Finding order by id={}", id);
    Assert.notNull(id);
    Order entity = orderRepository.findOne(id);
    if (entity == null) {
      String msg = String.format(NO_ORDER_FOUND_MSG, id);
      LOGGER.warn(msg);
      throw new ResourceNotFoundException(msg);
    }
    return entity;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.OrderService#addOrder(cat.grc.spring.data.dto.OrderDto)
   */
  @Override
  @Transactional
  public OrderDto addOrder(OrderDto order) {
    LOGGER.debug("Adding new order {}", order);
    Assert.notNull(order);
    Assert.notEmpty(order.getItems());
    boolean exists = order.getId() == null ? false : orderRepository.exists(order.getId());
    if (exists) {
      String msg = String.format("Order found for id=%d", order.getId());
      LOGGER.warn(msg);
      throw new ResourceAlreadyExistsException(msg);
    }
    Order savedEntity = orderRepository.save(refreshOrderDto(order));
    return modelMapper.map(savedEntity, OrderDto.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.OrderService#updateOrder(cat.grc.spring.data.dto.OrderDto)
   */
  @Override
  @Transactional
  public OrderDto updateOrder(OrderDto order) {
    orderMustExists(order.getId());
    return modelMapper.map(updateOrder(modelMapper.map(order, Order.class)), OrderDto.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.OrderService#deleteOrder(java.lang.Long)
   */
  @Override
  @Transactional
  public void deleteOrder(Long id) {
    LOGGER.debug("Deleting order by id={}", id);
    Assert.notNull(id);
    Order entity = findOrder(id);
    // Don't delete orders with invoices created
    if (!entity.getInvoices().isEmpty()) {
      String msg = String.format("Impossible to delete invoice id=%d because it has invoices created", id);
      LOGGER.warn(msg);
      throw new OrderWithInvoicesException(msg);
    }
    orderRepository.delete(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Collection<OrderItemDto> findItemsByOrderId(Long orderId, int page, int size) {
    LOGGER.debug("Finding items for orderId={} page={} and size={}", orderId, page, size);
    Assert.notNull(orderId);
    Pageable pageable = new PageRequest(page, size);
    Page<OrderItem> itemsPage = orderItemRepository.findByOrder(orderId, pageable);
    return itemsPage.getContent().stream().map(item -> modelMapper.map(item, OrderItemDto.class))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public OrderItemDto addOrderItem(OrderItemDto item) {
    LOGGER.debug("Adding item {} in a order", item);
    Assert.notNull(item);
    boolean exists = item.getId() == null ? false : orderItemRepository.exists(item.getId());
    if (exists) {
      String msg = String.format("Order Item found for id=%d", item.getId());
      LOGGER.warn(msg);
      throw new ResourceAlreadyExistsException(msg);
    }
    Order order = findOrder(item.getOrderId());
    OrderItem newItem = modelMapper.map(item, OrderItem.class);
    newItem.setOrder(order);
    order.getItems().add(newItem);
    Order updatedOrder = updateOrder(order);
    return modelMapper.map(
        updatedOrder.getItems().stream().sorted((o1, o2) -> o2.getId().compareTo(o1.getId())).findFirst().get(),
        OrderItemDto.class);
  }

  @Override
  @Transactional
  public OrderItemDto updateOrderItem(OrderItemDto item) {
    LOGGER.debug("Updating item {} in a order", item);
    Assert.notNull(item);
    Order order = findOrder(item.getOrderId());
    boolean result = order.getItems().removeIf(oldItem -> oldItem.getId().equals(item.getId()));
    if (!result) {
      String msg = String.format("Order Item not found for id=%d", item.getId());
      LOGGER.warn(msg);
      throw new ResourceNotFoundException(msg);
    }
    OrderItem updatedItem = modelMapper.map(item, OrderItem.class);
    updatedItem.setOrder(order);
    order.getItems().add(updatedItem);
    Order updatedOrder = updateOrder(order);
    return modelMapper.map(
        updatedOrder.getItems().stream().filter(savedItem -> savedItem.getId().equals(item.getId())).findFirst().get(),
        OrderItemDto.class);
  }

  @Override
  @Transactional
  public void deleteOrderItem(Long id) {
    LOGGER.debug("Removing itemId {} in a order", id);
    OrderItem item = orderItemRepository.findOne(id);
    if (item == null) {
      String msg = String.format("Order Item not found for id=%d", id);
      LOGGER.warn(msg);
      throw new ResourceNotFoundException(msg);
    }
    Order order = findOrder(item.getId());
    boolean deleted = order.getItems().removeIf(itemToDelete -> itemToDelete.getId().equals(id));
    LOGGER.debug("Item with id={} deleted?{}", id, deleted);
    updateOrder(order);
  }

  @Resource
  public void setOrderRepository(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Resource
  public void setModelMapper(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Resource
  public void setProductService(ProductServicePkg productService) {
    this.productService = productService;
  }

  @Resource
  public void setOrderItemRepository(OrderItemRepository orderItemRepository) {
    this.orderItemRepository = orderItemRepository;
  }

  private boolean orderMustExists(Long id) {
    Assert.notNull(id);
    boolean result = orderRepository.exists(id);
    if (!result) {
      String msg = String.format(NO_ORDER_FOUND_MSG, id);
      LOGGER.warn(msg);
      throw new ResourceNotFoundException(msg);
    }
    return result;
  }

  /**
   * Refresh the products in items with the last copy from ProductService. Calculate the price for
   * each item in the order and updates the total cost of the order
   * 
   * @param originalOrder
   * @return the original order updated
   */
  private Order refreshOrderDto(OrderDto originalOrder) {
    return refreshOrder(modelMapper.map(originalOrder, Order.class));
  }

  private Order refreshOrder(Order originalOrder) {
    LOGGER.debug("Refreshing order {}", originalOrder);
    Order order = new Order(originalOrder.getId(), originalOrder.getCustomer(), originalOrder.getPlaced(),
        originalOrder.getTotal());
    order.setItems(new HashSet<>());
    order.getItems().clear();

    originalOrder.getItems().forEach(itemDto -> {
      Product product = productService.findProductEntityById(itemDto.getProduct().getId());
      BigDecimal updatedCost = product.getPrice().multiply(new BigDecimal(itemDto.getQuantity()));
      order.getItems().add(new OrderItem(itemDto.getId(), order, product, itemDto.getQuantity(), updatedCost));
    });
    BigDecimal updatedTotalCost =
        new BigDecimal(order.getItems().stream().mapToDouble(item -> item.getCost().doubleValue()).sum()).setScale(3,
            RoundingMode.HALF_EVEN);
    order.setTotal(updatedTotalCost);
    return order;
  }

  private Order updateOrder(Order order) {
    LOGGER.debug("Updating order {}", order);
    Assert.notNull(order);
    Assert.notEmpty(order.getItems());
    return orderRepository.save(refreshOrder(order));
  }

}
