/**
 * 
 */
package cat.grc.spring.mvc.rest.controller;

import java.util.Collection;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cat.grc.spring.data.dto.OrderDto;
import cat.grc.spring.data.dto.OrderItemDto;
import cat.grc.spring.data.service.OrderService;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RestController
public class OrderController {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

  private OrderService service;

  @RequestMapping("/customers/{customerId}/orders")
  public Collection<OrderDto> getOrdersOfACustomer(@PathVariable Long customerId,
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "15") int size) {
    LOGGER.debug("Finding oders for customerId={} page={}, size={}", customerId, page, size);
    return service.findOrdersByCustomerId(customerId, page, size);
  }

  @RequestMapping(value = {"/customers/{customerId}/orders", "/orders"}, method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public OrderDto createOrder(@RequestBody OrderDto order) {
    LOGGER.debug("Creating order={} for customerId={}", order);
    return service.addOrder(order);
  }

  @RequestMapping(value = {"/customers/{customerId}/orders/{id}", "/orders/{id}"})
  public OrderDto getOrder(@PathVariable Long id) {
    LOGGER.debug("Finding orderId={}", id);
    return service.findOrderById(id);
  }

  @RequestMapping(value = {"/customers/{customerId}/orders/{id}", "/orders/{id}"}, method = RequestMethod.PUT)
  public OrderDto updateOrder(@PathVariable Long id, @RequestBody OrderDto order) {
    LOGGER.debug("Updating orderId={} order={}", id, order);
    return service.updateOrder(order);
  }

  @RequestMapping(value = {"/customers/{customerId}/orders/{id}", "/orders/{id}"}, method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteOrder(@PathVariable Long id) {
    LOGGER.debug("Deleting orderId={}", id);
    service.deleteOrder(id);
  }

  @RequestMapping(value = {"/customers/{customerId}/orders/{id}/items", "/orders/{id}/items"})
  public Collection<OrderItemDto> getOrderItems(@PathVariable Long id, @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "15") int size) {
    LOGGER.debug("Finding items for orderId={}", id);
    return service.findItemsByOrderId(id, page, size);
  }

  @RequestMapping(value = {"/customers/{customerId}/orders/{id}/items", "/orders/{id}/items"},
      method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public OrderItemDto createOrderItem(@PathVariable Long id, @RequestBody OrderItemDto item) {
    LOGGER.debug("Adding item={} for orderId={}", item, id);
    return service.addOrderItem(item);
  }

  @RequestMapping(value = {"/customers/{customerId}/orders/{orderId}/items/{id}", "/orders/{orderId}/items/{id}"},
      method = RequestMethod.PUT)
  public OrderItemDto updateOrderItem(@PathVariable Long orderId, @PathVariable Long id,
      @RequestBody OrderItemDto item) {
    LOGGER.debug("Updating item={} for orderId={} itemId={}", item, orderId, id);
    return service.updateOrderItem(item);
  }

  @RequestMapping(value = {"/customers/{customerId}/orders/{orderId}/items/{id}", "/orders/{orderId}/items/{id}"},
      method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteOrderItem(@PathVariable Long orderId, @PathVariable Long id) {
    LOGGER.debug("Deleting item={} for orderId={} for customerId={}", id, orderId);
    service.deleteOrderItem(id);
  }

  @Resource
  public void setService(OrderService service) {
    this.service = service;
  }

}
