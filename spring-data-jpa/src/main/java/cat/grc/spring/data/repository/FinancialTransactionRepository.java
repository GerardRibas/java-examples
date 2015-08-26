/**
 * 
 */
package cat.grc.spring.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cat.grc.spring.data.entity.FinancialTransaction;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface FinancialTransactionRepository extends PagingAndSortingRepository<FinancialTransaction, Long> {

  @Query("select f from FinancialTransaction f where f.invoice.number = ?1")
  Page<FinancialTransaction> findByInvoice(Long invoice, Pageable pageable);

}
