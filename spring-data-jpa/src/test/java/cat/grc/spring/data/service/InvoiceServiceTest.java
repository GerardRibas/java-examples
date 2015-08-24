package cat.grc.spring.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

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
import cat.grc.spring.data.entity.Invoice;
import cat.grc.spring.data.entity.InvoiceLineItem;
import cat.grc.spring.data.entity.Order;
import cat.grc.spring.data.entity.OrderItem;
import cat.grc.spring.data.entity.Product;
import cat.grc.spring.data.entity.ProductCategory;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.repository.InvoiceRepository;

public class InvoiceServiceTest {

  private InvoiceServiceImpl service;

  @Mock
  private InvoiceRepository invoiceRepository;

  @Mock
  private OrderServiceImpl orderService;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    service = new InvoiceServiceImpl();
    service.setInvoiceRepository(invoiceRepository);
    service.setOrderService(orderService);
    service.setModelMapper(new EntityManagerConfiguration().modelMapper());
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
    order.setItems(Arrays.asList(new OrderItem(1L, order, new Product(1L, null, productCategory, "Some Product Name",
        new BigDecimal("11.33"), "Blue", "10x10", "Some Product Description"), 1L, new BigDecimal("11.33"))));

    Invoice invoice = new Invoice(order, in.getCreated());

    Invoice savedInvoice = new Invoice(order, in.getCreated());
    savedInvoice.setNumber(1L);

    InvoiceLineItem lineItem = savedInvoice.getLines().iterator().next();
    lineItem.setId(1L);

    InvoiceDto expectedDto = new InvoiceDto();
    expectedDto.setCreated(savedInvoice.getCreated());
    expectedDto.setNumber(savedInvoice.getNumber());
    expectedDto.setOrderId(savedInvoice.getOrder().getId());

    InvoiceLineItemDto expectedInvoiceItem = new InvoiceLineItemDto();
    expectedInvoiceItem.setId(1L);
    expectedInvoiceItem.setDerivedProductCost(lineItem.getDerivedProductCost());
    expectedInvoiceItem.setDerivedTotalCost(lineItem.getDerivedTotalCost());
    expectedInvoiceItem.setDerivedVatPayable(lineItem.getDerivedVatPayable());
    expectedInvoiceItem.setInvoice(expectedDto);
    expectedInvoiceItem.setItemId(lineItem.getItem().getId());
    expectedInvoiceItem.setPrice(lineItem.getPrice());
    expectedInvoiceItem.setProductId(lineItem.getProduct().getId());
    expectedInvoiceItem.setProductTitle(lineItem.getProductTitle());
    expectedInvoiceItem.setQuantity(lineItem.getQuantity());
    expectedDto.setLines(Arrays.asList(expectedInvoiceItem));

    when(orderService.findOrder(eq(orderId))).thenReturn(order);
    when(invoiceRepository.save(eq(invoice))).thenReturn(savedInvoice);

    InvoiceDto result = service.createInvoiceFromOrder(in);
    assertEquals("Expected an invoice", expectedDto, result);

    verify(orderService).findOrder(eq(orderId));
    verify(invoiceRepository).save(eq(invoice));
    verifyNoMoreInteractions(orderService);
    verifyNoMoreInteractions(invoiceRepository);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testCreateInvoiceFromOrder_UnsupportedOperationException() {
    service.setOrderService(mock(OrderService.class));
    InvoiceDto in = new InvoiceDto(1L, new Date());
    service.createInvoiceFromOrder(in);
  }

  @Test
  public void testDeleteInvoice() {
    Long invoiceId = 1L;
    when(invoiceRepository.exists(eq(invoiceId))).thenReturn(true);
    service.deleteInvoice(invoiceId);
    verify(invoiceRepository).exists(eq(invoiceId));
    verify(invoiceRepository).delete(eq(invoiceId));
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testDeleteInvoice_ResourceNotFoundException() {
    Long invoiceId = 1L;
    when(invoiceRepository.exists(eq(invoiceId))).thenReturn(false);
    service.deleteInvoice(invoiceId);
  }

}
