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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cat.grc.spring.data.dto.ProductCategoryDto;
import cat.grc.spring.data.dto.ProductDto;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.service.ProductService;
import cat.grc.spring.mvc.rest.Application;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ContextConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class ProductControllerTest {

  private MockMvc mvc;

  private ObjectMapper objectMapper;

  @Mock
  private ProductService service;

  @Before
  public void before() {
    objectMapper = new ObjectMapper();
    MockitoAnnotations.initMocks(this);
    ProductController controller = new ProductController();
    controller.setService(service);
    mvc = standaloneSetup(controller).setHandlerExceptionResolvers(createExceptionResolver()).build();
  }

  @Test
  public void testGetProducts() throws Exception {
    ProductCategoryDto category = new ProductCategoryDto(1L, "Pets", new Float("0.2"));
    ProductDto parent = new ProductDto(1L, null, category, "Parent Product", new BigDecimal("10.000"), "Blue", "10x10",
        "Parent Product Description");
    ProductDto child = new ProductDto(2L, parent, category, "Child Product", new BigDecimal("15.000"), "Red", "20x20",
        "Child Product Description");
    Collection<ProductDto> products = Arrays.asList(parent, child);
    when(service.findAllProducts(eq(0), eq(15))).thenReturn(products);

    mvc.perform(get("/products").accept(APPLICATION_JSON_UTF8)).andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON_UTF8)).andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(parent.getId().intValue())))
        .andExpect(jsonPath("$[0].category.code", is(parent.getCategory().getCode().intValue())))
        .andExpect(jsonPath("$[0].category.description", is(parent.getCategory().getDescription())))
        .andExpect(jsonPath("$[0].category.vatRating", is(0.2))).andExpect(jsonPath("$[0].name", is(parent.getName())))
        .andExpect(jsonPath("$[0].price", is(10.0))).andExpect(jsonPath("$[0].color", is(parent.getColor())))
        .andExpect(jsonPath("$[0].size", is(parent.getSize())))
        .andExpect(jsonPath("$[0].description", is(parent.getDescription())))
        .andExpect(jsonPath("$[1].id", is(child.getId().intValue())))
        .andExpect(jsonPath("$[1].parent.id", is(child.getParent().getId().intValue())))
        .andExpect(jsonPath("$[1].category.code", is(child.getCategory().getCode().intValue())))
        .andExpect(jsonPath("$[1].category.description", is(child.getCategory().getDescription())))
        .andExpect(jsonPath("$[1].category.vatRating", is(0.2))).andExpect(jsonPath("$[1].name", is(child.getName())))
        .andExpect(jsonPath("$[1].price", is(15.0))).andExpect(jsonPath("$[1].color", is(child.getColor())))
        .andExpect(jsonPath("$[1].size", is(child.getSize())))
        .andExpect(jsonPath("$[1].description", is(child.getDescription())));
    verify(service).findAllProducts(eq(0), eq(15));
  }

  @Test
  public void testCreateProduct() throws JsonProcessingException, Exception {
    ProductCategoryDto category = new ProductCategoryDto(1L, "Pets", new Float("0.2"));
    ProductDto product = new ProductDto(null, null, category, "Product", new BigDecimal("10.000"), "Blue", "10x10",
        "Product Description");
    ProductDto productSaved =
        new ProductDto(1L, null, category, "Product", new BigDecimal("10.000"), "Blue", "10x10", "Product Description");

    when(service.addProduct(eq(product))).thenReturn(productSaved);

    mvc.perform(post("/products").accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(product))).andExpect(status().isCreated())
        .andExpect(content().contentType(APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is(productSaved.getId().intValue())))
        .andExpect(jsonPath("$.category.code", is(productSaved.getCategory().getCode().intValue())))
        .andExpect(jsonPath("$.category.description", is(productSaved.getCategory().getDescription())))
        .andExpect(jsonPath("$.category.vatRating", is(0.2))).andExpect(jsonPath("$.name", is(productSaved.getName())))
        .andExpect(jsonPath("$.price", is(10.0))).andExpect(jsonPath("$.color", is(productSaved.getColor())))
        .andExpect(jsonPath("$.size", is(productSaved.getSize())))
        .andExpect(jsonPath("$.description", is(productSaved.getDescription())))
        .andExpect(header().string("Location", "http://localhost/products/1"));
    verify(service).addProduct(eq(product));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testCreateProduct_ResourceAlreadyExistsException() throws JsonProcessingException, Exception {
    ProductCategoryDto category = new ProductCategoryDto(1L, "Pets", new Float("0.2"));
    ProductDto product =
        new ProductDto(1L, null, category, "Product", new BigDecimal("10.000"), "Blue", "10x10", "Product Description");

    when(service.addProduct(Mockito.any(ProductDto.class))).thenThrow(ResourceAlreadyExistsException.class);

    mvc.perform(post("/products").accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(product))).andExpect(status().isConflict())
        .andExpect(jsonPath("$.code", is("E001")));

    verify(service).addProduct(eq(product));
  }

  @Test
  public void testFindProduct() throws JsonProcessingException, Exception {
    Long id = 1L;
    ProductCategoryDto category = new ProductCategoryDto(1L, "Pets", new Float("0.2"));
    ProductDto product =
        new ProductDto(id, null, category, "Product", new BigDecimal("10.000"), "Blue", "10x10", "Product Description");
    when(service.findProductById(eq(id))).thenReturn(product);

    mvc.perform(get("/products/1")).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is(product.getId().intValue())))
        .andExpect(jsonPath("$.category.code", is(product.getCategory().getCode().intValue())))
        .andExpect(jsonPath("$.category.description", is(product.getCategory().getDescription())))
        .andExpect(jsonPath("$.category.vatRating", is(0.2))).andExpect(jsonPath("$.name", is(product.getName())))
        .andExpect(jsonPath("$.price", is(10.0))).andExpect(jsonPath("$.color", is(product.getColor())))
        .andExpect(jsonPath("$.size", is(product.getSize())))
        .andExpect(jsonPath("$.description", is(product.getDescription())));
    verify(service).findProductById(eq(id));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testFindProduct_ResourceNotFoundException() throws JsonProcessingException, Exception {
    Long id = 1L;
    when(service.findProductById(eq(id))).thenThrow(ResourceNotFoundException.class);
    mvc.perform(get("/products/1")).andExpect(status().isNotFound())
        .andExpect(content().contentType(APPLICATION_JSON_UTF8)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("E002")));
    verify(service).findProductById(eq(id));
  }

  @Test
  public void testUpdateProduct() throws Exception {
    Long id = 1L;
    ProductCategoryDto category = new ProductCategoryDto(1L, "Pets", new Float("0.2"));
    ProductDto product =
        new ProductDto(id, null, category, "Product", new BigDecimal("10.000"), "Blue", "10x10", "Product Description");
    when(service.updateProduct(eq(product))).thenReturn(product);
    mvc.perform(put("/products/1").accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(product))).andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is(product.getId().intValue())))
        .andExpect(jsonPath("$.category.code", is(product.getCategory().getCode().intValue())))
        .andExpect(jsonPath("$.category.description", is(product.getCategory().getDescription())))
        .andExpect(jsonPath("$.category.vatRating", is(0.2))).andExpect(jsonPath("$.name", is(product.getName())))
        .andExpect(jsonPath("$.price", is(10.0))).andExpect(jsonPath("$.color", is(product.getColor())))
        .andExpect(jsonPath("$.size", is(product.getSize())))
        .andExpect(jsonPath("$.description", is(product.getDescription())));
    verify(service).updateProduct(eq(product));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateProduct_ResourceNotFoundException() throws JsonProcessingException, Exception {
    Long id = 1L;
    ProductCategoryDto category = new ProductCategoryDto(1L, "Pets", new Float("0.2"));
    ProductDto product =
        new ProductDto(id, null, category, "Product", new BigDecimal("10.000"), "Blue", "10x10", "Product Description");
    when(service.updateProduct(eq(product))).thenThrow(ResourceNotFoundException.class);
    mvc.perform(put("/products/1").accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(product))).andExpect(status().isNotFound())
        .andExpect(content().contentType(APPLICATION_JSON_UTF8)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("E002")));
    verify(service).updateProduct(eq(product));
  }

  @Test
  public void testDeleteProduct() throws Exception {
    Long id = 1L;
    mvc.perform(delete("/products/1")).andExpect(status().isNoContent());
    verify(service).deleteProduct(eq(id));
  }

  @Test
  public void testDeleteProduct_ResourceNotFoundException() throws Exception {
    Long id = 1L;
    doThrow(ResourceNotFoundException.class).when(service).deleteProduct(id);
    mvc.perform(delete("/products/1")).andExpect(status().isNotFound())
        .andExpect(content().contentType(APPLICATION_JSON_UTF8)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("E002")));
    verify(service).deleteProduct(eq(id));
  }



}
