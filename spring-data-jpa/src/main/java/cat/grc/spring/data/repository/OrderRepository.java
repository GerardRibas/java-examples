package cat.grc.spring.data.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import cat.grc.spring.data.entity.Order;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {

}
