package cat.grc.spring.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cat.grc.spring.data.entity.InvoiceLineItem;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface InvoiceLineItemRepository extends PagingAndSortingRepository<InvoiceLineItem, Long> {

  @Query("select i from InvoiceLineItem i where i.invoice.number = ?1")
  Page<InvoiceLineItem> findByInvoice(Long invoiceNumber, Pageable pageable);

}
