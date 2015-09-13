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

import java.util.Arrays;

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

import cat.grc.spring.data.dto.TransactionTypeDto;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.exception.TransactionTypeHasTransactionsException;
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
public class TransactionTypeControllerTest {

  private MockMvc mvc;

  private ObjectMapper objectMapper;

  @Mock
  private FinancialTransactionService service;

  @Before
  public void before() {
    objectMapper = new ObjectMapper();
    MockitoAnnotations.initMocks(this);
    TransactionTypeController controller = new TransactionTypeController();
    controller.setService(service);
    mvc = standaloneSetup(controller).setHandlerExceptionResolvers(createExceptionResolver()).build();
  }

  @Test
  public void testGetTransactionTypes() throws Exception {
    TransactionTypeDto type = new TransactionTypeDto(1L, "Pets");
    when(service.findAllTransactionTypes(eq(0), eq(15))).thenReturn(Arrays.asList(type));
    mvc.perform(get("/types")).andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].code", is(1)))
        .andExpect(jsonPath("$[0].description", is(type.getDescription()))).andExpect(status().isOk());
    verify(service).findAllTransactionTypes(eq(0), eq(15));
  }

  @Test
  public void testCreateTransactionType() throws JsonProcessingException, Exception {
    TransactionTypeDto type = new TransactionTypeDto(null, "Pets");
    TransactionTypeDto savedType = new TransactionTypeDto(1L, "Pets");
    when(service.addTransactionType(eq(type))).thenReturn(savedType);
    mvc.perform(post("/types").contentType(APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(type)))
        .andExpect(status().isCreated()).andExpect(jsonPath("$.code", is(1)))
        .andExpect(jsonPath("$.description", is(type.getDescription())));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCreateTransactionType_ResourceAlreadyExistsException() throws JsonProcessingException, Exception {
    TransactionTypeDto type = new TransactionTypeDto(null, "Pets");
    when(service.addTransactionType(eq(type))).thenThrow(ResourceAlreadyExistsException.class);
    mvc.perform(post("/types").contentType(APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(type)))
        .andExpect(status().isConflict()).andExpect(jsonPath("$.code", is("E001")));
    verify(service).addTransactionType(eq(type));
  }

  @Test
  public void testFindTransaction() throws Exception {
    TransactionTypeDto type = new TransactionTypeDto(1L, "Pets");
    when(service.findTransactionTypeById(eq(type.getCode()))).thenReturn(type);
    mvc.perform(get("/types/1")).andExpect(status().isOk()).andExpect(jsonPath("$.code", is(1)))
        .andExpect(jsonPath("$.description", is(type.getDescription())));
    verify(service).findTransactionTypeById(eq(type.getCode()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testFindTransaction_ResourceNotFoundException() throws Exception {
    when(service.findTransactionTypeById(eq(999L))).thenThrow(ResourceNotFoundException.class);
    mvc.perform(get("/types/999")).andExpect(status().isNotFound()).andExpect(jsonPath("$.code", is("E002")));
    verify(service).findTransactionTypeById(eq(999L));
  }

  @Test
  public void testUpdateTransactionType() throws Exception {
    TransactionTypeDto type = new TransactionTypeDto(1L, "Pets");
    when(service.updateTransactionType(eq(type))).thenReturn(type);
    mvc.perform(put("/types/1").contentType(APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(type)))
        .andExpect(status().isOk()).andExpect(jsonPath("$.code", is(1)))
        .andExpect(jsonPath("$.description", is(type.getDescription())));
    verify(service).updateTransactionType(eq(type));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateTransactionType_ResourceNotFoundException() throws Exception {
    TransactionTypeDto type = new TransactionTypeDto(1L, "Pets");
    when(service.updateTransactionType(eq(type))).thenThrow(ResourceNotFoundException.class);
    mvc.perform(put("/types/1").contentType(APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(type)))
        .andExpect(status().isNotFound()).andExpect(jsonPath("$.code", is("E002")));
    verify(service).updateTransactionType(eq(type));
  }

  @Test
  public void testDeleteTransactionType() throws Exception {
    mvc.perform(delete("/types/1")).andExpect(status().isNoContent());
    verify(service).deleteTransactionType(1L);
  }

  @Test
  public void testDeleteTransactionType_ResourceNotFoundException() throws Exception {
    doThrow(ResourceNotFoundException.class).when(service).deleteTransactionType(eq(1L));
    mvc.perform(delete("/types/1")).andExpect(status().isNotFound()).andExpect(jsonPath("$.code", is("E002")));
    verify(service).deleteTransactionType(1L);
  }

  @Test
  public void testDeleteTransactionType_TransactionTypeHasTransactionsException() throws Exception {
    doThrow(TransactionTypeHasTransactionsException.class).when(service).deleteTransactionType(eq(1L));
    mvc.perform(delete("/types/1")).andExpect(status().isConflict()).andExpect(jsonPath("$.code", is("E008")));
    verify(service).deleteTransactionType(1L);
  }

}
