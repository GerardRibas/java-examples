package cat.grc.spring.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import cat.grc.spring.data.EntityManagerConfiguration;
import cat.grc.spring.data.dto.InvoiceDto;
import cat.grc.spring.data.dto.InvoiceLineItemDto;
import cat.grc.spring.data.entity.Customer;
import cat.grc.spring.data.entity.FinancialTransaction;
import cat.grc.spring.data.entity.Invoice;
import cat.grc.spring.data.entity.InvoiceLineItem;
import cat.grc.spring.data.entity.Order;
import cat.grc.spring.data.entity.OrderItem;
import cat.grc.spring.data.entity.Product;
import cat.grc.spring.data.entity.ProductCategory;
import cat.grc.spring.data.exception.InvoiceWithTransactionsException;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.repository.FinancialTransactionRepository;
import cat.grc.spring.data.repository.InvoiceLineItemRepository;
import cat.grc.spring.data.repository.InvoiceRepository;

public class InvoiceServiceTest {

  private InvoiceServiceImpl service;

  @Mock
  private InvoiceRepository invoiceRepository;

  @Mock
  private OrderServicePkg orderService;

  @Mock
  private FinancialTransactionRepository financialTransactionRepository;

  @Mock
  private InvoiceLineItemRepository invoiceLineItemRepository;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    service = new InvoiceServiceImpl();
    service.setInvoiceRepository(invoiceRepository);
    service.setOrderService(orderService);
    service.setModelMapper(new EntityManagerConfiguration().modelMapper());
    service.setInvoiceLineItemRepository(invoiceLineItemRepository);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindInvoicesByCustomer() {
    Long customerId = 1L;
    int page = 1;
    int size = 15;

    Pageable pageable = new PageRequest(1, 15);
    Page<Invoice> pageInvoice = mock(Page.class);
    Invoice result = mock(Invoice.class);
    when(pageInvoice.getContent()).thenReturn(Arrays.asList(result));
    when(invoiceRepository.findByCustomer(eq(customerId), eq(pageable))).thenReturn(pageInvoice);
    Collection<InvoiceDto> invoices = service.findInvoicesByCustomer(customerId, page, size);
    assertFalse("Expected a non empty collection", invoices.isEmpty());
    assertEquals("Expected only one result", 1, invoices.size());

    verify(invoiceRepository).findByCustomer(eq(customerId), eq(pageable));
    verifyNoMoreInteractions(invoiceRepository);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindInvoicesByOrder() {
    Long orderId = 1L;
    int page = 1;
    int size = 15;

    Pageable pageable = new PageRequest(1, 15);
    Page<Invoice> pageInvoice = mock(Page.class);
    Invoice result = mock(Invoice.class);
    when(pageInvoice.getContent()).thenReturn(Arrays.asList(result));
    when(invoiceRepository.findByOrder(eq(orderId), eq(pageable))).thenReturn(pageInvoice);
    Collection<InvoiceDto> invoices = service.findInvoicesByOrder(orderId, page, size);
    assertFalse("Expected a non empty collection", invoices.isEmpty());
    assertEquals("Expected only one result", 1, invoices.size());

    verify(invoiceRepository).findByOrder(eq(orderId), eq(pageable));
    verifyNoMoreInteractions(invoiceRepository);
  }

  @Test
  public void testCreateInvoiceFromOrder() {
    Long orderId = 1L;
    InvoiceDto in = new InvoiceDto(orderId, new Date());
    Order order = new Order(1L, new Customer(1L), new Date(), new BigDecimal("11.33"));
    ProductCategory productCategory = new ProductCategory(1L, "Some Category", 0.2F);
    order.setItems(
        new HashSet<>(Arrays.asList(new OrderItem(1L, order, new Product(1L, null, productCategory, "Some Product Name",
            new BigDecimal("11.33"), "Blue", "10x10", "Some Product Description"), 1L, new BigDecimal("11.33")))));

    Invoice invoice = new Invoice(order, in.getCreated());

    Invoice savedInvoice = new Invoice(order, in.getCreated());
    savedInvoice.setNumber(1L);

    InvoiceLineItem lineItem = savedInvoice.getLines().iterator().next();
    lineItem.setId(1L);

    InvoiceDto expectedDto = new InvoiceDto();
    expectedDto.setCreated(savedInvoice.getCreated());
    expectedDto.setNumber(savedInvoice.getNumber());
    expectedDto.setOrderId(savedInvoice.getOrder().getId());

    when(orderService.findOrder(eq(orderId))).thenReturn(order);
    when(invoiceRepository.save(eq(invoice))).thenReturn(savedInvoice);

    InvoiceDto result = service.createInvoice(in);
    assertEquals("Expected an invoice", expectedDto, result);

    verify(orderService).findOrder(eq(orderId));
    verify(invoiceRepository).save(eq(invoice));
    verifyNoMoreInteractions(orderService);
    verifyNoMoreInteractions(invoiceRepository);
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testCreateInvoiceFromOrder_ResourceAlreadyExistsException() {
    Long orderId = 1L;
    InvoiceDto in = new InvoiceDto(orderId, new Date());
    in.setNumber(1L);
    when(invoiceRepository.exists(eq(in.getNumber()))).thenReturn(true);
    service.createInvoice(in);
  }

  @Test
  public void testFindInvoiceById() {
    Long invoiceId = 1L;
    Invoice invoice = mock(Invoice.class);
    when(invoiceRepository.findOne(eq(invoiceId))).thenReturn(invoice);
    InvoiceDto result = service.findInvoiceById(invoiceId);
    assertNotNull("Expected an invoice", result);
    verify(invoiceRepository).findOne(eq(invoiceId));
  }

  @Test
  public void testDeleteInvoice() {
    Long invoiceId = 1L;
    Invoice invoice = mock(Invoice.class);
    when(invoiceRepository.findOne(eq(invoiceId))).thenReturn(invoice);
    service.deleteInvoice(invoiceId);
    verify(invoiceRepository).findOne(eq(invoiceId));
    verify(invoiceRepository).delete(eq(invoiceId));
  }

  @Test(expected = InvoiceWithTransactionsException.class)
  public void testDeleteInvoice_InvoiceWithTransactionsException() {
    Long invoiceId = 1L;
    Invoice invoice = mock(Invoice.class);
    FinancialTransaction transaction = mock(FinancialTransaction.class);
    when(invoice.getTransactions()).thenReturn(Arrays.asList(transaction));
    when(invoiceRepository.findOne(eq(invoiceId))).thenReturn(invoice);
    service.deleteInvoice(invoiceId);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testDeleteInvoice_ResourceNotFoundException() {
    Long invoiceId = 1L;
    when(invoiceRepository.exists(eq(invoiceId))).thenReturn(false);
    service.deleteInvoice(invoiceId);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindInvoiceLineItemsOfInvoice() {
    Long invoiceNumber = 1L;
    int page = 1;
    int size = 15;

    Pageable pageable = new PageRequest(1, 15);
    Page<InvoiceLineItem> pageInvoice = mock(Page.class);
    InvoiceLineItem result = mock(InvoiceLineItem.class);
    when(pageInvoice.getContent()).thenReturn(Arrays.asList(result));
    when(invoiceLineItemRepository.findByInvoice(eq(invoiceNumber), eq(pageable))).thenReturn(pageInvoice);
    Collection<InvoiceLineItemDto> lines = service.findInvoiceLineItemsOfInvoice(invoiceNumber, page, size);
    assertFalse("Expected a non empty collection", lines.isEmpty());
    assertEquals("Expected only one result", 1, lines.size());

    verify(invoiceLineItemRepository).findByInvoice(eq(invoiceNumber), eq(pageable));
    verifyNoMoreInteractions(invoiceRepository);
  }

}
