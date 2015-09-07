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

import cat.grc.spring.data.dto.InvoiceDto;
import cat.grc.spring.data.dto.InvoiceLineItemDto;
import cat.grc.spring.data.entity.Invoice;
import cat.grc.spring.data.entity.InvoiceLineItem;
import cat.grc.spring.data.entity.Order;
import cat.grc.spring.data.exception.InvoiceWithTransactionsException;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.repository.InvoiceLineItemRepository;
import cat.grc.spring.data.repository.InvoiceRepository;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@Service
public class InvoiceServiceImpl implements InvoiceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceServiceImpl.class);

  private InvoiceRepository invoiceRepository;

  private InvoiceLineItemRepository invoiceLineItemRepository;

  private OrderServicePkg orderService;

  private ModelMapper modelMapper;

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.InvoiceService#findInvoicesByCustomer(java.lang.Long, int,
   * int)
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

  /*
   * (non-Javadoc)
   * 
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

  @Override
  @Transactional(readOnly = true)
  public InvoiceDto findInvoiceById(Long id) {
    return modelMapper.map(findInvoiceEntityById(id), InvoiceDto.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.InvoiceService#createInvoiceFromOrder(cat.grc.spring.data.dto.
   * InvoiceDto)
   */
  @Override
  @Transactional
  public InvoiceDto createInvoice(InvoiceDto invoice) {
    LOGGER.debug("Adding a new invoice for {}", invoice);
    Assert.notNull(invoice);
    Assert.notNull(invoice.getOrderId());
    Assert.notNull(invoice.getCreated());
    boolean exists = invoice.getNumber() == null ? false : invoiceRepository.exists(invoice.getNumber());
    if (exists) {
      String msg = String.format("Invoice already exists with id=%d", invoice.getNumber());
      LOGGER.error(msg);
      throw new ResourceAlreadyExistsException(msg);
    }
    Order order = orderService.findOrder(invoice.getOrderId());
    Invoice entity = invoiceRepository.save(new Invoice(order, invoice.getCreated()));
    return modelMapper.map(entity, InvoiceDto.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.InvoiceService#deleteInvoice(java.lang.Long)
   */
  @Override
  @Transactional
  public void deleteInvoice(Long id) {
    LOGGER.debug("Deleting invoice by id {}", id);
    Assert.notNull(id);
    Invoice invoice = findInvoiceEntityById(id);
    if (!invoice.getTransactions().isEmpty()) {
      String msg = String.format("Invoice=%d has transactions", id);
      LOGGER.error(msg);
      throw new InvoiceWithTransactionsException(msg);
    }
    invoiceRepository.delete(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Collection<InvoiceLineItemDto> findInvoiceLineItemsOfInvoice(Long invoiceNumber, int page, int size) {
    LOGGER.debug("Finding line items of an invoice by invoiceNumber={}", invoiceNumber);
    Assert.notNull(invoiceNumber);
    Pageable pageable = new PageRequest(page, size);
    Page<InvoiceLineItem> invoicesPage = invoiceLineItemRepository.findByInvoice(invoiceNumber, pageable);
    return invoicesPage.getContent().stream()
        .map(invoiceLineItem -> modelMapper.map(invoiceLineItem, InvoiceLineItemDto.class))
        .collect(Collectors.toList());
  }

  @Resource
  public void setInvoiceRepository(InvoiceRepository invoiceRepository) {
    this.invoiceRepository = invoiceRepository;
  }

  @Resource
  public void setOrderService(OrderServicePkg orderService) {
    this.orderService = orderService;
  }

  @Resource
  public void setModelMapper(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Resource
  public void setInvoiceLineItemRepository(InvoiceLineItemRepository invoiceLineItemRepository) {
    this.invoiceLineItemRepository = invoiceLineItemRepository;
  }

  private Invoice findInvoiceEntityById(Long id) {
    LOGGER.debug("Find invoice by id={}", id);
    Invoice invoice = invoiceRepository.findOne(id);
    if (invoice == null) {
      String msg = String.format("Invoice does not exists with id=%d", id);
      LOGGER.error(msg);
      throw new ResourceNotFoundException(msg);
    }
    return invoice;
  }

}
