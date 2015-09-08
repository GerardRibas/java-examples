/**
 * 
 */
package cat.grc.spring.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cat.grc.spring.data.entity.Account;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {

  @Query("select a from Account a where a.customer.id = ?1")
  Page<Account> findByCustomer(Long customerId, Pageable pageable);

}
