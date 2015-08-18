/**
 * 
 */
package cat.grc.spring.data.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import cat.grc.spring.data.entity.Account;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {

}
