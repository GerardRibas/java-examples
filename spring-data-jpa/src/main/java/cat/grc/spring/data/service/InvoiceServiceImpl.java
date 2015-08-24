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
import cat.grc.spring.data.entity.Invoice;
import cat.grc.spring.data.entity.Order;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.repository.InvoiceRepository;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@Service
public class InvoiceServiceImpl {

  private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceServiceImpl.class);

  private InvoiceRepository invoiceRepository;

  private OrderService orderService;

  private ModelMapper modelMapper;

  @Transactional(readOnly = true)
  public Collection<InvoiceDto> findInvoicesByCustomer(Long customerId, int page, int size) {
    LOGGER.debug("Finding invoices by customerId={}", customerId);
    Assert.notNull(customerId);
    Pageable pageable = new PageRequest(page, size);
    Page<Invoice> invoicesPage = invoiceRepository.findByCustomer(customerId, pageable);
    return invoicesPage.getContent().stream().map(invoice -> modelMapper.map(invoice, InvoiceDto.class))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Collection<InvoiceDto> findInvoicesByOrder(Long orderId, int page, int size) {
    LOGGER.debug("Finding invoices by orderId={}", orderId);
    Assert.notNull(orderId);
    Pageable pageable = new PageRequest(page, size);
    Page<Invoice> invoicesPage = invoiceRepository.findByOrder(orderId, pageable);
    return invoicesPage.getContent().stream().map(invoice -> modelMapper.map(invoice, InvoiceDto.class))
        .collect(Collectors.toList());
  }

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

  @Transactional
  public void deleteInvoice(Long id) {
    LOGGER.debug("Deleting invoice by id {}", id);
    Assert.notNull(id);
    boolean exists = invoiceRepository.exists(id);
    if (!exists) {
      String msg = String.format("Invoice does not exists with id=%d", id);
      LOGGER.error(msg);
      throw new ResourceNotFoundException(msg);
    }
    invoiceRepository.delete(id);
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
  public void setModelMapper(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

}
