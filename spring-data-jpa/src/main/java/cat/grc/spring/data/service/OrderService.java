package cat.grc.spring.data.service;

import java.util.Collection;

import cat.grc.spring.data.dto.OrderDto;


public interface OrderService {

  Collection<OrderDto> findOrdersByCustomerId(Long customerId, int page, int size);

  OrderDto findOrderById(Long id);

  OrderDto addOrder(OrderDto order);

  OrderDto updateOrder(OrderDto order);

  /**
   * It only succeeds if the order doesn't have any invoices created
   * 
   * @param id of the order to delete
   */
  void deleteOrder(Long id);

}
