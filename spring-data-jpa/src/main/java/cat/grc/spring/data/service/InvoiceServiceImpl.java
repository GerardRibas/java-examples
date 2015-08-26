/**
 * 
 */
package cat.grc.spring.data.service;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import cat.grc.spring.data.dto.FinancialTransactionDto;
import cat.grc.spring.data.dto.InvoiceDto;
import cat.grc.spring.data.entity.FinancialTransaction;
import cat.grc.spring.data.entity.Invoice;
import cat.grc.spring.data.entity.Order;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.repository.FinancialTransactionRepository;
import cat.grc.spring.data.repository.InvoiceRepository;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@Service
public class InvoiceServiceImpl implements InvoiceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceServiceImpl.class);

  private InvoiceRepository invoiceRepository;

  private OrderService orderService;

  private FinancialTransactionRepository financialTransactionRepository;

  private ModelMapper modelMapper;

  /* (non-Javadoc)
   * @see cat.grc.spring.data.service.InvoiceService#findInvoicesByCustomer(java.lang.Long, int, int)
   */
  @Override
  @Transactional(readOnly = true)
  public Collection<InvoiceDto> findInvoicesByCustomer(Long customerId, int page, int size) {
    LOGGER.debug("Finding invoices by customerId={}", customerId);
    Assert.notNull(customerId);
    Pageable pageable = new PageRequest(page, size);
    Page<Invoice> invoicesPage = invoiceRepository.findByCustomer(customerId, pageable);
    return invoicesPage.getContent().stream().map(invoice -> modelMapper.map(invoice, InvoiceDto.class))
        .collect(Collectors.toList());
  }

  /* (non-Javadoc)
   * @see cat.grc.spring.data.service.InvoiceService#findInvoicesByOrder(java.lang.Long, int, int)
   */
  @Override
  @Transactional(readOnly = true)
  public Collection<InvoiceDto> findInvoicesByOrder(Long orderId, int page, int size) {
    LOGGER.debug("Finding invoices by orderId={}", orderId);
    Assert.notNull(orderId);
    Pageable pageable = new PageRequest(page, size);
    Page<Invoice> invoicesPage = invoiceRepository.findByOrder(orderId, pageable);
    return invoicesPage.getContent().stream().map(invoice -> modelMapper.map(invoice, InvoiceDto.class))
        .collect(Collectors.toList());
  }

  /* (non-Javadoc)
   * @see cat.grc.spring.data.service.InvoiceService#createInvoiceFromOrder(cat.grc.spring.data.dto.InvoiceDto)
   */
  @Override
  @Transactional
  public InvoiceDto createInvoiceFromOrder(InvoiceDto invoice) {
    LOGGER.debug("Adding a new invoice for {}", invoice);
    Assert.notNull(invoice);
    Assert.notNull(invoice.getOrderId());
    Assert.notNull(invoice.getCreated());
    if (orderService instanceof OrderServiceImpl) {
      Order order = ((OrderServiceImpl) orderService).findOrder(invoice.getOrderId());
      Assert.notNull(order);
      Invoice entity = invoiceRepository.save(new Invoice(order, invoice.getCreated()));
      return modelMapper.map(entity, InvoiceDto.class);
    } else {
      throw new UnsupportedOperationException("Impossible to found Order, no implementation found");
    }
  }

  /* (non-Javadoc)
   * @see cat.grc.spring.data.service.InvoiceService#deleteInvoice(java.lang.Long)
   */
  @Override
  @Transactional
  public void deleteInvoice(Long id) {
    LOGGER.debug("Deleting invoice by id {}", id);
    Assert.notNull(id);
    invoiceMustExists(id);
    invoiceRepository.delete(id);
  }

  /* (non-Javadoc)
   * @see cat.grc.spring.data.service.InvoiceService#findTransactionById(java.lang.Long)
   */
  @Override
  @Transactional(readOnly = true)
  public FinancialTransactionDto findTransactionById(Long id) {
    LOGGER.debug("Find transaction by id={}", id);
    FinancialTransaction transaction = financialTransactionRepository.findOne(id);
    if (transaction == null) {
      String msg = String.format("Transaction does not exists with id=%d", id);
      LOGGER.error(msg);
      throw new ResourceNotFoundException(msg);
    }
    return modelMapper.map(transaction, FinancialTransactionDto.class);
  }

  /* (non-Javadoc)
   * @see cat.grc.spring.data.service.InvoiceService#findTransactionsByInvoiceNumber(java.lang.Long, int, int)
   */
  @Override
  @Transactional(readOnly = true)
  public Collection<FinancialTransactionDto> findTransactionsByInvoiceNumber(Long number, int page, int size) {
    LOGGER.debug("Find transaction by InvoiceNumber={}", number);
    invoiceMustExists(number);
    Pageable pageable = new PageRequest(page, size);
    Page<FinancialTransaction> transactionsPage = financialTransactionRepository.findByInvoice(number, pageable);
    return transactionsPage.getContent().stream()
        .map(transaction -> modelMapper.map(transaction, FinancialTransactionDto.class)).collect(Collectors.toList());
  }

  /* (non-Javadoc)
   * @see cat.grc.spring.data.service.InvoiceService#payInvoice(cat.grc.spring.data.dto.FinancialTransactionDto)
   */
  @Override
  @Transactional
  public FinancialTransactionDto payInvoice(FinancialTransactionDto transaction) {
    LOGGER.debug("Paying transaction={}", transaction);
    Assert.notNull(transaction);
    Assert.notNull(transaction.getAccountId());
    Assert.notNull(transaction.getInvoiceNumber());
    Assert.notNull(transaction.getTypeCode());
    invoiceMustExists(transaction.getInvoiceNumber());
    FinancialTransaction savedTransaction =
        financialTransactionRepository.save(modelMapper.map(transaction, FinancialTransaction.class));
    return modelMapper.map(savedTransaction, FinancialTransactionDto.class);
  }

  /* (non-Javadoc)
   * @see cat.grc.spring.data.service.InvoiceService#deleteFinancialTransaction(java.lang.Long)
   */
  @Override
  @Transactional
  public void deleteFinancialTransaction(Long id) {
    LOGGER.debug("Deleting transaction by id {}", id);
    transactionMustExists(id);
    financialTransactionRepository.delete(id);
  }

  @Resource
  public void setInvoiceRepository(InvoiceRepository invoiceRepository) {
    this.invoiceRepository = invoiceRepository;
  }

  @Resource
  public void setOrderService(OrderService orderService) {
    this.orderService = orderService;
  }

  @Resource
  public void setFinancialTransactionRepository(FinancialTransactionRepository financialTransactionRepository) {
    this.financialTransactionRepository = financialTransactionRepository;
  }

  @Resource
  public void setModelMapper(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  private boolean transactionMustExists(Long id) {
    Assert.notNull(id);
    boolean exists = financialTransactionRepository.exists(id);
    if (!exists) {
      String msg = String.format("Transaction does not exists with id=%d", id);
      LOGGER.error(msg);
      throw new ResourceNotFoundException(msg);
    }
    return exists;
  }

  private boolean invoiceMustExists(Long id) {
    Assert.notNull(id);
    boolean exists = invoiceRepository.exists(id);
    if (!exists) {
      String msg = String.format("Invoice does not exists with id=%d", id);
      LOGGER.error(msg);
      throw new ResourceNotFoundException(msg);
    }
    return exists;
  }

}
