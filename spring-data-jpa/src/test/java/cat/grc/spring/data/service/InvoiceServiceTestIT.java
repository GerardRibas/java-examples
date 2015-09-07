/**
 * 
 */
package cat.grc.spring.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
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
import cat.grc.spring.data.dto.InvoiceDto;
import cat.grc.spring.data.dto.InvoiceLineItemDto;
import cat.grc.spring.data.exception.InvoiceWithTransactionsException;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EntityManagerConfiguration.class})
@Transactional
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class})
@DatabaseSetup("InvoiceServiceTestIT_setup.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InvoiceServiceTestIT {

  @Autowired
  private InvoiceService service;

  @Autowired
  private EntityManager entityManager;

  @Test
  public void testFindInvoicesByCustomer() {
    Long customerId = 1L;
    Collection<InvoiceDto> invoices = service.findInvoicesByCustomer(customerId, 0, 15);
    assertFalse("Expected invoices", invoices.isEmpty());
  }

  @Test
  public void testFindInvoiceByOrder() {
    Long orderId = 1L;
    Collection<InvoiceDto> invoices = service.findInvoicesByOrder(orderId, 0, 15);
    assertFalse("Expected invoices", invoices.isEmpty());
  }

  @Test
  public void testFindInvoiceById() {
    Long invoiceNumber = 1L;
    InvoiceDto invoice = service.findInvoiceById(invoiceNumber);
    assertNotNull("Expected an invoice", invoice);
    assertEquals("Expected the same invoice number", invoiceNumber, invoice.getNumber());
  }

  @Test
  @ExpectedDatabase(value = "InvoiceServiceTestIT.testCreateInvoiceFromOrder.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testCreateInvoiceFromOrder() {
    Date created = Date.from(LocalDate.of(2015, 9, 5).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    InvoiceDto invoice = new InvoiceDto(3L, created);
    InvoiceLineItemDto item = new InvoiceLineItemDto();
    item.setInvoice(invoice);
    item.setItemId(3L);
    invoice.setLines(Arrays.asList(item));
    InvoiceDto savedInvoice = service.createInvoice(invoice);
    assertNotNull("Expected invoice", savedInvoice);
    assertNotNull("Expected invoice number", savedInvoice.getNumber());
    entityManager.flush();
  }

  @Test
  @ExpectedDatabase(value = "InvoiceServiceTestIT.testDeleteInvoice.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testDeleteInvoice() {
    service.deleteInvoice(1L);
    entityManager.flush();
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testDeleteInvoice_ResourceNotFoundException() {
    service.deleteInvoice(99999L);
  }

  @Test(expected = InvoiceWithTransactionsException.class)
  public void testDeleteInvoice_InvoiceWithTransactionsException() {
    service.deleteInvoice(2L);
  }

  @Test
  public void testFindInvoiceLineItemsOfInvoice() {
    Collection<InvoiceLineItemDto> lines = service.findInvoiceLineItemsOfInvoice(1L, 0, 15);
    assertFalse("Expected lines for that invoice", lines.isEmpty());
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testCreateInvoiceFromOrder_ResourceAlreadyExistsException() {
    Long orderId = 1L;
    InvoiceDto in = new InvoiceDto(orderId, new Date());
    in.setNumber(1L);
    service.createInvoice(in);
  }

}
