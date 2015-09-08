/**
 * 
 */
package cat.grc.spring.data.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import cat.grc.spring.data.entity.TransactionType;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface TransactionTypeRepository extends PagingAndSortingRepository<TransactionType, Long> {

}
