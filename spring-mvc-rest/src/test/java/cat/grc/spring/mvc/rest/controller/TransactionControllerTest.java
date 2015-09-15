/**
 * 
 */
package cat.grc.spring.mvc.rest.controller;

import static cat.grc.spring.mvc.rest.controller.TestUtil.APPLICATION_JSON_UTF8;
import static cat.grc.spring.mvc.rest.controller.TestUtil.createExceptionResolver;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cat.grc.spring.data.dto.FinancialTransactionDto;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.service.FinancialTransactionService;
import cat.grc.spring.mvc.rest.Application;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ContextConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class TransactionControllerTest {

  private MockMvc mvc;

  private ObjectMapper objectMapper;

  @Mock
  private FinancialTransactionService service;

  @Before
  public void before() {
    objectMapper = new ObjectMapper();
    MockitoAnnotations.initMocks(this);
    TransactionController controller = new TransactionController();
    controller.setService(service);
    mvc = standaloneSetup(controller).setHandlerExceptionResolvers(createExceptionResolver()).build();
  }

  @Test
  public void testFindTransactionsForInvoice() throws Exception {
    FinancialTransactionDto transaction =
        new FinancialTransactionDto(1L, 1L, 1L, 1L, new Date(), new BigDecimal("10.44"));
    when(service.findTransactionsByInvoiceNumber(eq(transaction.getInvoiceNumber()), eq(0), eq(15)))
        .thenReturn(Arrays.asList(transaction));

    mvc.perform(get("/invoices/1/transactions")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(1))).andExpect(jsonPath("$[0].accountId", is(1)))
        .andExpect(jsonPath("$[0].invoiceNumber", is(1))).andExpect(jsonPath("$[0].typeCode", is(1)))
        .andExpect(jsonPath("$[0].transactionDate", is(transaction.getTransactionDate().getTime())))
        .andExpect(jsonPath("$[0].amount", is(transaction.getAmount().doubleValue())));

    verify(service).findTransactionsByInvoiceNumber(eq(transaction.getInvoiceNumber()), eq(0), eq(15));
  }

  @Test
  public void testCreateTransactionInvoicesPath() throws JsonProcessingException, Exception {
    testCreateTransaction("/invoices/1/transactions");
  }

  @Test
  public void testCreateTransaction() throws JsonProcessingException, Exception {
    testCreateTransaction("/transactions");
  }

  @Test
  public void testCreateTransactionInvoicesPath_ResourceAlreadyExistsException() throws Exception {
    testCreateTransaction_ResourceAlreadyExistsException("/invoices/1/transactions");
  }

  @Test
  public void testCreateTransaction_ResourceAlreadyExistsException() throws Exception {
    testCreateTransaction_ResourceAlreadyExistsException("/transactions");
  }

  @SuppressWarnings("unchecked")
  private void testCreateTransaction_ResourceAlreadyExistsException(String url) throws Exception {
    FinancialTransactionDto transaction =
        new FinancialTransactionDto(1L, 1L, 1L, 1L, new Date(), new BigDecimal("10.44"));
    when(service.payInvoice(eq(transaction))).thenThrow(ResourceAlreadyExistsException.class);

    mvc.perform(post(url).contentType(APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(transaction)))
        .andExpect(status().isConflict()).andExpect(jsonPath("$.code", is("E001")));

    verify(service).payInvoice(eq(transaction));
  }

  @Test
  public void testFindFinancialTransaction() throws Exception {
    FinancialTransactionDto transaction =
        new FinancialTransactionDto(1L, 1L, 1L, 1L, new Date(), new BigDecimal("10.44"));
    when(service.findTransactionById(eq(transaction.getId()))).thenReturn(transaction);

    mvc.perform(get("/transactions/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.accountId", is(1))).andExpect(jsonPath("$.invoiceNumber", is(1)))
        .andExpect(jsonPath("$.typeCode", is(1)))
        .andExpect(jsonPath("$.transactionDate", is(transaction.getTransactionDate().getTime())))
        .andExpect(jsonPath("$.amount", is(transaction.getAmount().doubleValue())));

    verify(service).findTransactionById(eq(transaction.getId()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testFindFinancialTransaction_ResourceNotFoundException() throws Exception {
    when(service.findTransactionById(eq(1L))).thenThrow(ResourceNotFoundException.class);
    mvc.perform(get("/transactions/1")).andExpect(status().isNotFound()).andExpect(jsonPath("$.code", is("E002")));
    verify(service).findTransactionById(eq(1L));
  }

  @Test
  public void testUpdateFinancialTransaction() throws JsonProcessingException, Exception {
    FinancialTransactionDto transaction =
        new FinancialTransactionDto(1L, 1L, 1L, 1L, new Date(), new BigDecimal("10.44"));
    when(service.updateFinancialTransaction(eq(transaction))).thenReturn(transaction);
    mvc.perform(
        put("/transactions/1").contentType(APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(transaction)))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id", is(1))).andExpect(jsonPath("$.accountId", is(1)))
        .andExpect(jsonPath("$.invoiceNumber", is(1))).andExpect(jsonPath("$.typeCode", is(1)))
        .andExpect(jsonPath("$.transactionDate", is(transaction.getTransactionDate().getTime())))
        .andExpect(jsonPath("$.amount", is(transaction.getAmount().doubleValue())));
    verify(service).updateFinancialTransaction(eq(transaction));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateFinancialTransaction_ResourceNotFoundException() throws JsonProcessingException, Exception {
    FinancialTransactionDto transaction =
        new FinancialTransactionDto(1L, 1L, 1L, 1L, new Date(), new BigDecimal("10.44"));
    when(service.updateFinancialTransaction(eq(transaction))).thenThrow(ResourceNotFoundException.class);
    mvc.perform(
        put("/transactions/1").contentType(APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(transaction)))
        .andExpect(status().isNotFound()).andExpect(jsonPath("$.code", is("E002")));
    verify(service).updateFinancialTransaction(eq(transaction));
  }

  @Test
  public void testDeleteTransaction() throws Exception {
    mvc.perform(delete("/transactions/1")).andExpect(status().isNoContent());
    verify(service).deleteFinancialTransaction(eq(1L));
  }

  @Test
  public void testDeleteTransaction_ResourceNotFoundException() throws Exception {
    doThrow(ResourceNotFoundException.class).when(service).deleteFinancialTransaction(eq(1L));
    mvc.perform(delete("/transactions/1")).andExpect(status().isNotFound()).andExpect(jsonPath("$.code", is("E002")));
    verify(service).deleteFinancialTransaction(eq(1L));
  }


  private void testCreateTransaction(String url) throws JsonProcessingException, Exception {
    FinancialTransactionDto transaction =
        new FinancialTransactionDto(null, 1L, 1L, 1L, new Date(), new BigDecimal("10.44"));
    FinancialTransactionDto savedTransaction =
        new FinancialTransactionDto(1L, 1L, 1L, 1L, new Date(), new BigDecimal("10.44"));
    when(service.payInvoice(eq(transaction))).thenReturn(savedTransaction);

    mvc.perform(post(url).contentType(APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(transaction)))
        .andExpect(status().isCreated()).andExpect(jsonPath("$.id", is(1)));

    verify(service).payInvoice(eq(transaction));
  }


}
