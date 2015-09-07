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
import java.util.Collections;
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
import cat.grc.spring.data.dto.FinancialTransactionDto;
import cat.grc.spring.data.dto.TransactionTypeDto;
import cat.grc.spring.data.entity.Account;
import cat.grc.spring.data.entity.Customer;
import cat.grc.spring.data.entity.FinancialTransaction;
import cat.grc.spring.data.entity.Invoice;
import cat.grc.spring.data.entity.Order;
import cat.grc.spring.data.entity.OrderItem;
import cat.grc.spring.data.entity.Product;
import cat.grc.spring.data.entity.ProductCategory;
import cat.grc.spring.data.entity.TransactionType;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.exception.TransactionTypeHasTransactionsException;
import cat.grc.spring.data.repository.FinancialTransactionRepository;
import cat.grc.spring.data.repository.TransactionTypeRepository;

public class FinancialTransactionServiceTest {

  private FinancialTrasactionServiceImpl service;

  @Mock
  private FinancialTransactionRepository financialTransactionRepository;

  @Mock
  private InvoiceService invoiceService;

  @Mock
  private TransactionTypeRepository transactionTypeRepository;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    service = new FinancialTrasactionServiceImpl();
    service.setFinancialTransactionRepository(financialTransactionRepository);
    service.setInvoiceService(invoiceService);
    service.setTransactionTypeRepository(transactionTypeRepository);
    service.setModelMapper(new EntityManagerConfiguration().modelMapper());
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
    verify(financialTransactionRepository).findByInvoice(eq(invoiceNumber), eq(pageable));
  }

  @Test
  public void testPayInvoice() {
    FinancialTransactionDto transaction =
        new FinancialTransactionDto(null, 1L, 1L, 1L, new Date(), new BigDecimal("11.33"));
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
    verify(financialTransactionRepository).save(eq(entity));
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testPayInvoice_ResourceAlreadyExistsException() {
    FinancialTransactionDto transaction =
        new FinancialTransactionDto(1L, 1L, 1L, 1L, new Date(), new BigDecimal("11.33"));
    when(financialTransactionRepository.exists(eq(transaction.getId()))).thenReturn(true);
    service.payInvoice(transaction);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = ResourceNotFoundException.class)
  public void testPayInvoice_InvoiceDoesntExists() {
    FinancialTransactionDto transaction =
        new FinancialTransactionDto(null, 1L, 1L, 1L, new Date(), new BigDecimal("11.33"));
    when(invoiceService.findInvoiceById(1L)).thenThrow(ResourceNotFoundException.class);
    service.payInvoice(transaction);
  }

  @Test
  public void testDeleteFinancialTransaction() {
    Long financialTransactionId = 1L;
    when(financialTransactionRepository.exists(financialTransactionId)).thenReturn(true);
    service.deleteFinancialTransaction(financialTransactionId);
    verify(financialTransactionRepository).exists(eq(financialTransactionId));
    verify(financialTransactionRepository).delete(eq(financialTransactionId));
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testDeleteFinancialTransaction_ResourceNotFoundException() {
    Long financialTransactionId = 1L;
    when(financialTransactionRepository.exists(financialTransactionId)).thenReturn(false);
    service.deleteFinancialTransaction(financialTransactionId);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindAllTransactionTypes() {
    int page = 1;
    int size = 1;
    Pageable pageable = new PageRequest(page, size);
    TransactionType type = new TransactionType(1L, "Payment");
    TransactionTypeDto expectedDto = new TransactionTypeDto(type.getCode(), type.getDescription());
    Page<TransactionType> transactionsPage = mock(Page.class);
    when(transactionTypeRepository.findAll(eq(pageable))).thenReturn(transactionsPage);
    when(transactionsPage.getContent()).thenReturn(Arrays.asList(type));
    Collection<TransactionTypeDto> types = service.findAllTransactionTypes(page, size);
    assertFalse("Expected values on it", types.isEmpty());
    assertEquals("Expected the same transaction type returned", expectedDto, types.iterator().next());
    verify(transactionTypeRepository).findAll(eq(pageable));
    verifyNoMoreInteractions(transactionTypeRepository);
  }

  @Test
  public void testFindTransactionTypeById() {
    Long transactionTypeId = 1L;
    TransactionType type = new TransactionType(transactionTypeId, "Payment");
    TransactionTypeDto expectedDto = new TransactionTypeDto(type.getCode(), type.getDescription());
    when(transactionTypeRepository.findOne(eq(transactionTypeId))).thenReturn(type);
    TransactionTypeDto dto = service.findTransactionTypeById(transactionTypeId);
    assertEquals("Expected the same transaction type returned", expectedDto, dto);
    verify(transactionTypeRepository).findOne(eq(transactionTypeId));
    verifyNoMoreInteractions(transactionTypeRepository);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testFindTransactionTypeById_ResourceNotFoundException() {
    Long transactionTypeId = 1L;
    when(transactionTypeRepository.findOne(eq(transactionTypeId))).thenReturn(null);
    service.findTransactionTypeById(transactionTypeId);
  }

  @Test
  public void testAddTransactionType() {
    TransactionTypeDto type = new TransactionTypeDto(null, "Payment");
    TransactionType entity = new TransactionType(type.getCode(), type.getDescription());
    TransactionType entitySaved = new TransactionType(1L, type.getDescription());
    TransactionTypeDto expectedDto = new TransactionTypeDto(entitySaved.getCode(), entitySaved.getDescription());
    when(transactionTypeRepository.save(eq(entity))).thenReturn(entitySaved);
    TransactionTypeDto dto = service.addTransactionType(type);
    assertEquals("Expected the same transaction type returned", expectedDto, dto);
    verify(transactionTypeRepository).save(eq(entity));
    verifyNoMoreInteractions(transactionTypeRepository);
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testAddTransactionType_ResourceAlreadyExistsException() {
    TransactionTypeDto type = new TransactionTypeDto(1L, "Payment");
    when(transactionTypeRepository.exists(eq(type.getCode()))).thenReturn(true);
    service.addTransactionType(type);
  }

  @Test
  public void testUpdateTransactionType() {
    TransactionTypeDto type = new TransactionTypeDto(1L, "Payment");
    TransactionType entity = new TransactionType(type.getCode(), type.getDescription());
    when(transactionTypeRepository.exists(eq(type.getCode()))).thenReturn(true);
    when(transactionTypeRepository.save(entity)).thenReturn(entity);
    TransactionTypeDto dto = service.updateTransactionType(type);
    assertEquals("Expected the same transaction type returned", type, dto);
    verify(transactionTypeRepository).exists(eq(type.getCode()));
    verify(transactionTypeRepository).save(eq(entity));
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testUpdateTransactionType_ResourceNotFoundException() {
    TransactionTypeDto type = new TransactionTypeDto(1L, "Payment");
    when(transactionTypeRepository.exists(eq(type.getCode()))).thenReturn(false);
    service.updateTransactionType(type);
  }

  @Test
  public void testDeleteTransactionType() {
    Long typeId = 1L;
    TransactionType entity = new TransactionType(typeId, "Payment");
    entity.setTransactions(Collections.emptyList());
    when(transactionTypeRepository.findOne(typeId)).thenReturn(entity);
    service.deleteTransactionType(typeId);
    verify(transactionTypeRepository).findOne(eq(typeId));
    verify(transactionTypeRepository).delete(eq(typeId));
  }

  @Test(expected = TransactionTypeHasTransactionsException.class)
  public void testDeleteTransactionType_TransactionTypeHasTransactionsException() {
    Long typeId = 1L;
    TransactionType entity = new TransactionType(typeId, "Payment");
    FinancialTransaction transaction = mock(FinancialTransaction.class);
    entity.setTransactions(Arrays.asList(transaction));
    when(transactionTypeRepository.findOne(typeId)).thenReturn(entity);
    service.deleteTransactionType(typeId);
  }

  @Test
  public void testUpdateFinancialTransaction() {
    FinancialTransactionDto transaction =
        new FinancialTransactionDto(1L, 1L, 1L, 1L, new Date(), new BigDecimal("11.33"));
    FinancialTransaction entity = new FinancialTransaction();
    Account account = new Account();
    account.setId(1L);
    entity.setAccount(account);
    entity.setAmount(new BigDecimal("11.33"));
    Invoice invoice = new Invoice();
    invoice.setNumber(1L);
    entity.setId(transaction.getId());
    entity.setInvoice(invoice);
    entity.setTransactionDate(transaction.getTransactionDate());
    TransactionType type = new TransactionType();
    type.setCode(1L);
    entity.setType(type);
    when(financialTransactionRepository.exists(transaction.getId())).thenReturn(true);
    when(financialTransactionRepository.save(eq(entity))).thenReturn(entity);

    FinancialTransactionDto dto = service.updateFinancialTransaction(transaction);
    assertEquals("Expected the same transaction returned", transaction, dto);
    verify(financialTransactionRepository).exists(eq(transaction.getId()));
    verify(financialTransactionRepository).save(eq(entity));
  }

  private FinancialTransaction stubFinancialTransaction() {
    Customer customer = new Customer(1L);
    Order order = new Order(1L, customer, new Date(), new BigDecimal("11.33"));
    Product product = new Product(1L, null, new ProductCategory(1L, "some category", new Float("0.2")), "Some name",
        new BigDecimal("11.33"), "Blue", "10x10", "Some descrption");
    order.setItems(new HashSet<>(Arrays.asList(new OrderItem(1L, order, product, 1L, new BigDecimal("11.33")))));
    Invoice invoice = new Invoice(order, new Date());
    Account account = new Account(1L, customer, new Date(), "Some account name");
    return new FinancialTransaction(1L, account, invoice, new TransactionType(), new Date(), new BigDecimal("11.33"));
  }

}
