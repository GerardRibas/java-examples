/**
 * 
 */
package cat.grc.spring.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cat.grc.spring.data.entity.OrderItem;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface OrderItemRepository extends PagingAndSortingRepository<OrderItem, Long> {

  @Query("select i from OrderItem i where i.order.id = ?1")
  Page<OrderItem> findByOrder(Long orderId, Pageable pageable);

}
