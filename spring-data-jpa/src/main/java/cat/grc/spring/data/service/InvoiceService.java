package cat.grc.spring.data.service;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

import cat.grc.spring.data.dto.FinancialTransactionDto;
import cat.grc.spring.data.dto.InvoiceDto;


public interface InvoiceService {

  Collection<InvoiceDto> findInvoicesByCustomer(Long customerId, int page, int size);

  Collection<InvoiceDto> findInvoicesByOrder(Long orderId, int page, int size);

  InvoiceDto createInvoiceFromOrder(InvoiceDto invoice);

  void deleteInvoice(Long id);

  FinancialTransactionDto findTransactionById(Long id);

  Collection<FinancialTransactionDto> findTransactionsByInvoiceNumber(Long number, int page, int size);

  FinancialTransactionDto payInvoice(FinancialTransactionDto transaction);

  void deleteFinancialTransaction(Long id);

}
