package cat.grc.spring.data.service;

import java.util.Collection;

import cat.grc.spring.data.dto.InvoiceDto;
import cat.grc.spring.data.dto.InvoiceLineItemDto;
import cat.grc.spring.data.exception.InvoiceWithTransactionsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;


public interface InvoiceService {

  /**
   * Find the customer invoices
   * 
   * @param customerId the id of a customer
   * @param page the page requested
   * @param size the size of the page
   * @return the invoices
   */
  Collection<InvoiceDto> findInvoicesByCustomer(Long customerId, int page, int size);

  /**
   * Fins all invoices that belongs of an order
   * 
   * @param orderId the id of the order
   * @param page the page requested
   * @param size the size of the page
   * @return the invoices
   */
  Collection<InvoiceDto> findInvoicesByOrder(Long orderId, int page, int size);

  /**
   * Create an invoice
   * 
   * @param invoice the new invoice
   * @return the invoice saved
   * @throws ResourceNotFoundException if the order of the invoice can't be found
   */
  InvoiceDto createInvoice(InvoiceDto invoice);

  /**
   * Delete an invoice
   * 
   * @param id of the invoice to delete
   * @throws ResourceNotFoundException if the invoice doesn't exists
   * @throws InvoiceWithTransactionsException if the invoice has transactions
   */
  void deleteInvoice(Long id);

  /**
   * Find an invoice with given id
   * 
   * @param id the invoice to search
   * @return the invoice
   */
  InvoiceDto findInvoiceById(Long id);

  /**
   * Find the line items of an invoice
   * 
   * @param invoiceNumber the invoice number
   * @param page the page requested
   * @param size the size
   * @return the line items of an invoice
   */
  Collection<InvoiceLineItemDto> findInvoiceLineItemsOfInvoice(Long invoiceNumber, int page, int size);

}
