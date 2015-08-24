package cat.grc.spring.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cat.grc.spring.data.entity.Invoice;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long> {

  @Query("select i from Invoice i where i.order.customer.id = ?1")
  Page<Invoice> findByCustomer(Long customerId, Pageable pageable);

  @Query("select i from Invoice i where i.order.id = ?1")
  Page<Invoice> findByOrder(Long orderId, Pageable pageable);

}
