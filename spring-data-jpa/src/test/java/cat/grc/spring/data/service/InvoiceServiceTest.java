package cat.grc.spring.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
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
import cat.grc.spring.data.dto.FinancialTransactionDto;
import cat.grc.spring.data.dto.InvoiceDto;
import cat.grc.spring.data.dto.InvoiceLineItemDto;
import cat.grc.spring.data.entity.Account;
import cat.grc.spring.data.entity.Customer;
import cat.grc.spring.data.entity.FinancialTransaction;
import cat.grc.spring.data.entity.Invoice;
import cat.grc.spring.data.entity.InvoiceLineItem;
import cat.grc.spring.data.entity.Order;
import cat.grc.spring.data.entity.OrderItem;
import cat.grc.spring.data.entity.Product;
import cat.grc.spring.data.entity.ProductCategory;
import cat.grc.spring.data.entity.TransactionType;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.repository.FinancialTransactionRepository;
import cat.grc.spring.data.repository.InvoiceRepository;

public class InvoiceServiceTest {

  private InvoiceServiceImpl service;

  @Mock
  private InvoiceRepository invoiceRepository;

  @Mock
  private OrderServiceImpl orderService;

  @Mock
  private FinancialTransactionRepository financialTransactionRepository;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    service = new InvoiceServiceImpl();
    service.setInvoiceRepository(invoiceRepository);
    service.setOrderService(orderService);
    service.setFinancialTransactionRepository(financialTransactionRepository);
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

  @Test
  public void testFindTransactionById() {
    Long transactionId = 1L;
    FinancialTransaction entity = stubFinancialTransaction();
    FinancialTransactionDto expectedDto = new FinancialTransactionDto(entity.getId(), entity.getAccount().getId(),
        entity.getInvoice().getNumber(), entity.getType().getCode(), entity.getTransactionDate(), entity.getAmount());
    when(financialTransactionRepository.findOne(eq(transactionId))).thenReturn(entity);
    FinancialTransactionDto dto = service.findTransactionById(transactionId);
    assertEquals("Expected the financial statement", expectedDto, dto);
    verify(financialTransactionRepository).findOne(eq(transactionId));
    verifyZeroInteractions(orderService);
    verifyZeroInteractions(invoiceRepository);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testFindTransactionById_ResourceNotFoundException() {
    Long transactionId = 1L;
    when(financialTransactionRepository.findOne(eq(transactionId))).thenReturn(null);
    service.findTransactionById(transactionId);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testfindTransactionsByInvoiceNumber() {
    Long invoiceNumber = 1L;
    int page = 1;
    int size = 1;
    when(invoiceRepository.exists(eq(invoiceNumber))).thenReturn(true);
    Pageable pageable = new PageRequest(page, size);
    FinancialTransaction entity = stubFinancialTransaction();
    FinancialTransactionDto expectedDto = new FinancialTransactionDto(entity.getId(), entity.getAccount().getId(),
        entity.getInvoice().getNumber(), entity.getType().getCode(), entity.getTransactionDate(), entity.getAmount());

    Collection<FinancialTransactionDto> expectedTransactions = Arrays.asList(expectedDto);
    Page<FinancialTransaction> transactionsPage = mock(Page.class);
    when(financialTransactionRepository.findByInvoice(eq(invoiceNumber), eq(pageable))).thenReturn(transactionsPage);
    when(transactionsPage.getContent()).thenReturn(Arrays.asList(entity));

    Collection<FinancialTransactionDto> transactions =
        service.findTransactionsByInvoiceNumber(invoiceNumber, page, size);
    assertEquals("Expected the same collection", expectedTransactions, transactions);
    verify(invoiceRepository).exists(eq(invoiceNumber));
    verify(financialTransactionRepository).findByInvoice(eq(invoiceNumber), eq(pageable));
    verifyZeroInteractions(orderService);
  }

  @Test
  public void testPayInvoice() {
    FinancialTransactionDto transaction =
        new FinancialTransactionDto(null, 1L, 1L, 1L, new Date(), new BigDecimal("11.33"));
    when(invoiceRepository.exists(eq(transaction.getInvoiceNumber()))).thenReturn(true);

    FinancialTransaction entity = new FinancialTransaction();
    Account account = new Account();
    account.setId(1L);
    entity.setAccount(account);
    entity.setAmount(new BigDecimal("11.33"));
    Invoice invoice = new Invoice();
    invoice.setNumber(1L);
    entity.setInvoice(invoice);
    entity.setTransactionDate(transaction.getTransactionDate());
    TransactionType type = new TransactionType();
    type.setCode(1L);
    entity.setType(type);

    FinancialTransaction savedEntity = stubFinancialTransaction();
    FinancialTransactionDto expectedDto = new FinancialTransactionDto(savedEntity.getId(),
        savedEntity.getAccount().getId(), savedEntity.getInvoice().getNumber(), savedEntity.getType().getCode(),
        savedEntity.getTransactionDate(), savedEntity.getAmount());

    when(financialTransactionRepository.save(eq(entity))).thenReturn(savedEntity);
    FinancialTransactionDto dto = service.payInvoice(transaction);
    assertEquals("Expected a saved transaction", expectedDto, dto);
    verify(invoiceRepository).exists(eq(transaction.getInvoiceNumber()));
    verify(financialTransactionRepository).save(eq(entity));
    verifyZeroInteractions(orderService);
  }

  @Test
  public void testDeleteFinancialTransaction() {
    Long financialTransactionId = 1L;
    when(financialTransactionRepository.exists(financialTransactionId)).thenReturn(true);
    service.deleteFinancialTransaction(financialTransactionId);
    verify(financialTransactionRepository).exists(eq(financialTransactionId));
    verify(financialTransactionRepository).delete(eq(financialTransactionId));
    verifyZeroInteractions(invoiceRepository);
    verifyZeroInteractions(orderService);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testDeleteFinancialTransaction_ResourceNotFoundException() {
    Long financialTransactionId = 1L;
    when(financialTransactionRepository.exists(financialTransactionId)).thenReturn(false);
    service.deleteFinancialTransaction(financialTransactionId);
  }

  private FinancialTransaction stubFinancialTransaction() {
    Customer customer = new Customer(1L);
    Order order = new Order(1L, customer, new Date(), new BigDecimal("11.33"));
    Product product = new Product(1L, null, new ProductCategory(1L, "some category", new Float("0.2")), "Some name",
        new BigDecimal("11.33"), "Blue", "10x10", "Some descrption");
    order.setItems(Arrays.asList(new OrderItem(1L, order, product, 1L, new BigDecimal("11.33"))));
    Invoice invoice = new Invoice(order, new Date());
    Account account = new Account(1L, customer, new Date(), "Some account name");
    return new FinancialTransaction(1L, account, invoice, new TransactionType(), new Date(), new BigDecimal("11.33"));
  }

}
