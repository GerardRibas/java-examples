package cat.grc.spring.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cat.grc.spring.data.entity.Order;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {

  @Query("select o from Order o where o.customer.id = ?1")
  Page<Order> findByCustomer(Long customerId, Pageable pageable);

}
