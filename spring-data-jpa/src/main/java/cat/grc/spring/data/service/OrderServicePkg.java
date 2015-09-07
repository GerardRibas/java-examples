/**
 * 
 */
package cat.grc.spring.data.service;

import cat.grc.spring.data.entity.Order;
import cat.grc.spring.data.exception.ResourceNotFoundException;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 * 
 */
interface OrderServicePkg {

  /**
   * Find an order with the id given.
   * 
   * @param id of the order
   * @return the order
   * @throws ResourceNotFoundException if the order doesn't exists.
   */
  public Order findOrder(Long id);

}
