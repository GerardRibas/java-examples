/**
 * 
 */
package cat.grc.spring.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import cat.grc.spring.data.EntityManagerConfiguration;
import cat.grc.spring.data.dto.FinancialTransactionDto;
import cat.grc.spring.data.dto.TransactionTypeDto;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.exception.TransactionTypeHasTransactionsException;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EntityManagerConfiguration.class})
@Transactional
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class})
@DatabaseSetup("FinancialTransactionServiceTestIT_setup.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FinancialTransactionServiceTestIT {

  @Autowired
  private FinancialTransactionService service;

  @Autowired
  private EntityManager entityManager;

  @Test
  public void testFindTransactonById() {
    FinancialTransactionDto transaction = service.findTransactionById(1L);
    assertNotNull("Expected a transaction returned", transaction);
    assertEquals("Expected a transaction returned", 1L, transaction.getId().longValue());
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testFindTransactonById_ResourceNotFoundException() {
    service.findTransactionById(9999L);
  }

  @Test
  public void testFindTransactionsByInvoiceNumber() {
    Collection<FinancialTransactionDto> transactions = service.findTransactionsByInvoiceNumber(2L, 0, 15);
    assertFalse("Expected transactions for this invoice", transactions.isEmpty());
  }

  @Test
  @ExpectedDatabase(value = "FinancialTransactionServiceTestIT.testPayInvoice.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testPayInvoice() {
    Date transactionDate =
        Date.from(LocalDate.of(2015, 9, 5).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    FinancialTransactionDto transaction =
        new FinancialTransactionDto(null, 1L, 1L, 1L, transactionDate, new BigDecimal("5.00"));
    FinancialTransactionDto savedTransaction = service.payInvoice(transaction);
    assertNotNull("Expected a returned transaction", savedTransaction);
    assertNotNull("Expected a transactionId", savedTransaction.getId());
    entityManager.flush();
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testPayInvoice_InvoiceNotExists() {
    Date transactionDate =
        Date.from(LocalDate.of(2015, 9, 5).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    FinancialTransactionDto transaction =
        new FinancialTransactionDto(null, 1L, 99999L, 1L, transactionDate, new BigDecimal("5.00"));
    service.payInvoice(transaction);
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testPayInvoice_ResourceAlreadyExistsException() {
    Date transactionDate =
        Date.from(LocalDate.of(2015, 9, 5).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    FinancialTransactionDto transaction =
        new FinancialTransactionDto(1L, 1L, 1L, 1L, transactionDate, new BigDecimal("5.00"));
    service.payInvoice(transaction);
  }

  @Test
  @ExpectedDatabase(value = "FinancialTransactionServiceTestIT.testDeleteFinancialTransaction.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testDeleteFinancialTransaction() {
    service.deleteFinancialTransaction(1L);
    entityManager.flush();
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testDeleteFinancialTransaction_ResourceNotFoundException() {
    service.deleteFinancialTransaction(999999L);
  }

  @Test
  public void testFindAllTransactionTypes() {
    Collection<TransactionTypeDto> transactionTypes = service.findAllTransactionTypes(0, 15);
    assertFalse("Expected a collection", transactionTypes.isEmpty());
  }

  @Test
  public void testFindTransactionTypeById() {
    TransactionTypeDto dto = service.findTransactionTypeById(1L);
    assertNotNull("Expected a transaction type", dto);
    assertEquals("Expected the same requested code", 1L, dto.getCode().longValue());
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testFindTransactionTypeById_ResourceNotFoundException() {
    service.findTransactionTypeById(99999L);
  }

  @Test
  @ExpectedDatabase(value = "FinancialTransactionServiceTestIT.testAddTransactionType.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testAddTransactionType() {
    TransactionTypeDto transactionType = new TransactionTypeDto(null, "Credit Card");
    TransactionTypeDto saved = service.addTransactionType(transactionType);
    assertNotNull("Expected a transaction type", saved);
    assertNotNull("Expected a transaction type code generated", saved.getCode());
    assertEquals("Expected the same description", transactionType.getDescription(), saved.getDescription());
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testAddTransactionType_ResourceAlreadyExistsException() {
    TransactionTypeDto transactionType = new TransactionTypeDto(1L, "Credit Card");
    service.addTransactionType(transactionType);
  }

  @Test
  @ExpectedDatabase(value = "FinancialTransactionServiceTestIT.testUpdateTransactionType.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testUpdateTransactionType() {
    TransactionTypeDto transactionType = new TransactionTypeDto(1L, "Cash Payment");
    TransactionTypeDto saved = service.updateTransactionType(transactionType);
    assertEquals("Expected saved the same transaction type", transactionType, saved);
    entityManager.flush();
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testUpdateTransactionType_ResourceNotFoundException() {
    TransactionTypeDto transactionType = new TransactionTypeDto(99999L, "Cash Payment");
    service.updateTransactionType(transactionType);
  }

  @Test
  @ExpectedDatabase(value = "FinancialTransactionServiceTestIT.testDeleteTransactionType.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testDeleteTransactionType() {
    service.deleteTransactionType(2L);
    entityManager.flush();
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testDeleteTransactionType_ResourceNotFoundException() {
    service.deleteTransactionType(9999L);
  }

  @Test(expected = TransactionTypeHasTransactionsException.class)
  public void testDeleteTransactionType_TransactionTypeHasTransactionsException() {
    service.deleteTransactionType(1L);
  }

  @Test
  @ExpectedDatabase(value = "FinancialTransactionServiceTestIT.testUpdateFinancialTransaction.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testUpdateFinancialTransaction() {
    Date transactionDate =
        Date.from(LocalDate.of(2015, 9, 6).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    FinancialTransactionDto transaction =
        new FinancialTransactionDto(1L, 1L, 2L, 1L, transactionDate, new BigDecimal("11.22"));
    FinancialTransactionDto dto = service.updateFinancialTransaction(transaction);
    entityManager.flush();
    assertNotNull("Expected the returned transaction saved", dto);
  }

}
