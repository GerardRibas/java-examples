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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import cat.grc.spring.data.dto.InvoiceDto;
import cat.grc.spring.data.dto.InvoiceLineItemDto;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.service.InvoiceService;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class InvoiceControllerTest {

  private MockMvc mvc;

  private ObjectMapper objectMapper;

  @Mock
  private InvoiceService service;

  @Before
  public void before() {
    objectMapper = new ObjectMapper();
    MockitoAnnotations.initMocks(this);
    InvoiceController controller = new InvoiceController();
    controller.setService(service);
    mvc = standaloneSetup(controller).setHandlerExceptionResolvers(createExceptionResolver()).build();
  }

  @Test
  public void testGetInvoicesByCustomer() throws Exception {
    InvoiceDto invoice = new InvoiceDto(1L, new Date());
    invoice.setNumber(1L);
    when(service.findInvoicesByCustomer(eq(1L), eq(0), eq(15))).thenReturn(Arrays.asList(invoice));
    mvc.perform(get("/customers/1/invoices")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].number", is(invoice.getNumber().intValue())))
        .andExpect(jsonPath("$[0].orderId", is(invoice.getOrderId().intValue())))
        .andExpect(jsonPath("$[0].created", is(invoice.getCreated().getTime())));
    verify(service).findInvoicesByCustomer(eq(1L), eq(0), eq(15));
  }

  @Test
  public void testCreateInvoice() throws Exception {
    testCreateInvoice("/invoices");
  }

  @Test
  public void testCreateInvoiceCustomersPath() throws Exception {
    testCreateInvoice("/customers/1/invoices");
  }

  @Test
  public void testFindInvoice() throws Exception {
    testFindInvoice("/invoices/1");
  }

  @Test
  public void testFindInvoiceCustomersPath() throws Exception {
    testFindInvoice("/customers/1/invoices/1");
  }

  @Test
  public void testFindInvoice_ResourceNotFoundException() throws Exception {
    testFindInvoice_ResourceNotFoundException("/invoices/999");
  }

  @Test
  public void testFindInvoiceCustomersPath_ResourceNotFoundException() throws Exception {
    testFindInvoice_ResourceNotFoundException("/customers/1/invoices/999");
  }

  @Test
  public void testDeleteInvoice() throws Exception {
    testDeleteInvoice("/invoices/1");
  }

  @Test
  public void testDeleteInvoiceCustomersPath() throws Exception {
    testDeleteInvoice("/customers/1/invoices/1");
  }

  @Test
  public void testDeleteInvoice_ResourceNotFoundException() throws Exception {
    testDeleteInvoice_ResourceNotFoundException("/invoices/999");
  }

  @Test
  public void testDeleteInvoiceCustomersPath_ResourceNotFoundException() throws Exception {
    testDeleteInvoice_ResourceNotFoundException("/customers/1/invoices/999");
  }

  @Test
  public void testFindInvoiceLineItems() throws Exception {
    testFindInvoiceLineItems("/invoices/1/lines");
  }

  @Test
  public void testFindInvoiceLineItemsCustomersPath() throws Exception {
    testFindInvoiceLineItems("/customers/1/invoices/1/lines");
  }

  @Test
  public void testGetInvoicesByOrder() throws Exception {
    testGetInvoices("/orders/1/invoices");
  }

  @Test
  public void testGetInvoicesByOrderCustomersPath() throws Exception {
    testGetInvoices("/customers/1/orders/1/invoices");
  }

  private void testCreateInvoice(String url) throws Exception {
    InvoiceDto invoice = new InvoiceDto(1L, new Date());
    invoice.setNumber(1L);
    when(service.createInvoice(invoice)).thenReturn(invoice);
    mvc.perform(post(url).contentType(APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(invoice)))
        .andExpect(status().isCreated()).andExpect(jsonPath("$.number", is(invoice.getNumber().intValue())))
        .andExpect(jsonPath("$.orderId", is(invoice.getOrderId().intValue())))
        .andExpect(jsonPath("$.created", is(invoice.getCreated().getTime())));
  }

  private void testGetInvoices(String url) throws Exception {
    InvoiceDto invoice = new InvoiceDto(1L, new Date());
    invoice.setNumber(1L);
    when(service.findInvoicesByOrder(eq(1L), eq(0), eq(15))).thenReturn(Arrays.asList(invoice));
    mvc.perform(get(url)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].number", is(invoice.getNumber().intValue())))
        .andExpect(jsonPath("$[0].orderId", is(invoice.getOrderId().intValue())))
        .andExpect(jsonPath("$[0].created", is(invoice.getCreated().getTime())));
    verify(service).findInvoicesByOrder(eq(1L), eq(0), eq(15));
  }

  private void testFindInvoiceLineItems(String url) throws Exception {
    InvoiceLineItemDto item = new InvoiceLineItemDto();
    item.setId(1L);
    item.setProductId(1L);
    item.setPrice(new BigDecimal("10.22"));
    item.setProductTitle("Some product title");
    item.setQuantity(1L);
    item.setItemId(1L);
    item.setDerivedProductCost(new BigDecimal("10.22"));
    item.setDerivedTotalCost(new BigDecimal("10.22"));
    item.setDerivedVatPayable(new BigDecimal("10.22"));

    when(service.findInvoiceLineItemsOfInvoice(eq(1L), eq(0), eq(15))).thenReturn(Arrays.asList(item));

    mvc.perform(get(url)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(item.getId().intValue())))
        .andExpect(jsonPath("$[0].itemId", is(item.getItemId().intValue())))
        .andExpect(jsonPath("$[0].productId", is(item.getProductId().intValue())))
        .andExpect(jsonPath("$[0].productTitle", is(item.getProductTitle())))
        .andExpect(jsonPath("$[0].quantity", is(item.getQuantity().intValue())))
        .andExpect(jsonPath("$[0].price", is(item.getPrice().doubleValue())))
        .andExpect(jsonPath("$[0].derivedProductCost", is(item.getDerivedProductCost().doubleValue())))
        .andExpect(jsonPath("$[0].derivedVatPayable", is(item.getDerivedVatPayable().doubleValue())))
        .andExpect(jsonPath("$[0].derivedTotalCost", is(item.getDerivedTotalCost().doubleValue())));

    verify(service).findInvoiceLineItemsOfInvoice(eq(1L), eq(0), eq(15));
  }

  private void testDeleteInvoice_ResourceNotFoundException(String url) throws Exception {
    InvoiceDto invoice = new InvoiceDto(999L, new Date());
    invoice.setNumber(999L);
    doThrow(ResourceNotFoundException.class).when(service).deleteInvoice(eq(999L));
    mvc.perform(delete(url)).andExpect(status().isNotFound());
    verify(service).deleteInvoice(eq(999L));
  }

  private void testDeleteInvoice(String url) throws Exception {
    InvoiceDto invoice = new InvoiceDto(1L, new Date());
    invoice.setNumber(1L);
    mvc.perform(delete(url)).andExpect(status().isNoContent());
    verify(service).deleteInvoice(eq(1L));
  }

  @SuppressWarnings("unchecked")
  private void testFindInvoice_ResourceNotFoundException(String url) throws Exception {
    InvoiceDto invoice = new InvoiceDto(999L, new Date());
    invoice.setNumber(999L);
    when(service.findInvoiceById(eq(999L))).thenThrow(ResourceNotFoundException.class);

    mvc.perform(get(url)).andExpect(status().isNotFound()).andExpect(jsonPath("$.code", is("E002")));

    verify(service).findInvoiceById(eq(999L));
  }

  private void testFindInvoice(String url) throws Exception {
    InvoiceDto invoice = new InvoiceDto(1L, new Date());
    invoice.setNumber(1L);
    when(service.findInvoiceById(eq(1L))).thenReturn(invoice);

    mvc.perform(get(url)).andExpect(status().isOk()).andExpect(jsonPath("$.number", is(invoice.getNumber().intValue())))
        .andExpect(jsonPath("$.orderId", is(invoice.getOrderId().intValue())))
        .andExpect(jsonPath("$.created", is(invoice.getCreated().getTime())));

    verify(service).findInvoiceById(eq(1L));
  }

}
