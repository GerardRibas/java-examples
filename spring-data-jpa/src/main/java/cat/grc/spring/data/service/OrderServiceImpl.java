/**
 * 
 */
package cat.grc.spring.data.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
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
import cat.grc.spring.data.entity.Order;
import cat.grc.spring.data.entity.OrderItem;
import cat.grc.spring.data.entity.Product;
import cat.grc.spring.data.exception.OrderWithInvoicesException;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.repository.OrderRepository;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

  private OrderRepository orderRepository;

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

  protected Order findOrder(Long id) {
    LOGGER.debug("Finding order by id={}", id);
    Assert.notNull(id);
    Order entity = orderRepository.findOne(id);
    if (entity == null) {
      String msg = String.format("No Order found for id=%d", id);
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
    LOGGER.debug("Updating order {}", order);
    Assert.notNull(order);
    Assert.notEmpty(order.getItems());
    orderMustExists(order.getId());
    Order savedEntity = orderRepository.save(refreshOrderDto(order));
    return modelMapper.map(savedEntity, OrderDto.class);
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
    Order entity = orderRepository.findOne(id);
    if (entity == null) {
      String msg = String.format("No Order found for id=%d", id);
      LOGGER.warn(msg);
      throw new ResourceNotFoundException(msg);
    }
    // Don't delete orders with invoices created
    if (!entity.getInvoices().isEmpty()) {
      String msg = String.format("Impossible to delete invoice id=%d because it has invoices created", id);
      LOGGER.warn(msg);
      throw new OrderWithInvoicesException(msg);
    }
    orderRepository.delete(id);
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

  private boolean orderMustExists(Long id) {
    Assert.notNull(id);
    boolean result = orderRepository.exists(id);
    if (!result) {
      String msg = String.format("No Order found for id=%d", id);
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
    LOGGER.debug("Refreshing orderDto {}", originalOrder);
    Order order = modelMapper.map(originalOrder, Order.class);
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

}
