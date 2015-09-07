package cat.grc.spring.data.service;

import java.util.Collection;

import cat.grc.spring.data.dto.OrderDto;
import cat.grc.spring.data.dto.OrderItemDto;
import cat.grc.spring.data.exception.OrderWithInvoicesException;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;


public interface OrderService {

  /**
   * Find customer orders
   * 
   * @param customerId the customer id
   * @param page the page requested
   * @param size the size of the page
   * @return the orders
   */
  Collection<OrderDto> findOrdersByCustomerId(Long customerId, int page, int size);

  /**
   * Find an order with the given id
   * 
   * @param id of the order
   * @return the order
   * @throws ResourceNotFoundException if the order doesn't exists
   */
  OrderDto findOrderById(Long id);

  /**
   * Add a new order
   * 
   * @param order the new order to add
   * @return the order saved
   * @throws ResourceAlreadyExistsException if the order was saved previously
   */
  OrderDto addOrder(OrderDto order);

  /**
   * Update the given order
   * 
   * @param order the order to update
   * @return the order updated
   * @throws ResourceNotFoundException if the order doesn't exists
   */
  OrderDto updateOrder(OrderDto order);

  /**
   * Delete an order
   * 
   * @param id of the order to delete
   * @throws ResourceNotFoundException if the order doesn't exists
   * @throws OrderWithInvoicesException if the order has invoices created
   */
  void deleteOrder(Long id);

  /**
   * Find items of an order
   * 
   * @param orderId the order id
   * @param page the page requested
   * @param size the size of the page
   * @return the items of an order
   */
  Collection<OrderItemDto> findItemsByOrderId(Long orderId, int page, int size);

  /**
   * Add an item in a order.
   * 
   * @param item the item to add
   * @return the item saved
   * @throws ResourceAlreadyExistsException if the item was saved previously
   */
  OrderItemDto addOrderItem(OrderItemDto item);

  /**
   * Update an item of an order
   * 
   * @param item to be updated
   * @return item updated
   * @throws ResourceNotFoundException if the item can't be found
   */
  OrderItemDto updateOrderItem(OrderItemDto item);

  /**
   * Delete item of an order
   * 
   * @param id of the item to be deleted
   * @throws ResourceNotFoundException if the item can't be found
   */
  void deleteOrderItem(Long id);

}
