/**
 * 
 */
package cat.grc.spring.data.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import cat.grc.spring.data.entity.OrderItem;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface OrderItemRepository extends PagingAndSortingRepository<OrderItem, Long> {

}
