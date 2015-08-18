/**
 * 
 */
package cat.grc.spring.data.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import cat.grc.spring.data.entity.Customer;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

}
