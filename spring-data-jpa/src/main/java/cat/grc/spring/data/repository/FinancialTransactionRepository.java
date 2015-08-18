/**
 * 
 */
package cat.grc.spring.data.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import cat.grc.spring.data.entity.FinancialTransaction;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface FinancialTransactionRepository extends PagingAndSortingRepository<FinancialTransaction, Long> {

}
