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
import cat.grc.spring.data.exception.ProductCategoryHasProductsException;
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
public class ProductCategoryControllerTest {

  private MockMvc mvc;

  private ObjectMapper objectMapper;

  @Mock
  private ProductService service;

  @Before
  public void before() {
    objectMapper = new ObjectMapper();
    MockitoAnnotations.initMocks(this);
    ProductCategoryController controller = new ProductCategoryController();
    controller.setService(service);
    mvc = standaloneSetup(controller).setHandlerExceptionResolvers(createExceptionResolver()).build();
  }

  @Test
  public void testGetProductCategories() throws Exception {
    ProductCategoryDto category = new ProductCategoryDto(1L, "Pets", new Float("0.2"));
    Collection<ProductCategoryDto> categories = Arrays.asList(category);
    when(service.findAllCategories(eq(0), eq(15))).thenReturn(categories);

    mvc.perform(get("/categories").accept(APPLICATION_JSON_UTF8)).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].code", is(1)))
        .andExpect(jsonPath("$[0].description", is("Pets"))).andExpect(jsonPath("$[0].vatRating", is(0.2)));

    verify(service).findAllCategories(eq(0), eq(15));
  }

  @Test
  public void testCreateProductCategory() throws JsonProcessingException, Exception {
    ProductCategoryDto category = new ProductCategoryDto(null, "Pets", new Float("0.2"));
    ProductCategoryDto savedCategory = new ProductCategoryDto(1L, "Pets", new Float("0.2"));
    when(service.addCategory(eq(category))).thenReturn(savedCategory);

    mvc.perform(post("/categories").accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(category))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.code", is(1))).andExpect(jsonPath("$.description", is(savedCategory.getDescription())))
        .andExpect(jsonPath("$.vatRating", is(0.2)))
        .andExpect(header().string("Location", "http://localhost/categories/1"));

    verify(service).addCategory(eq(category));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCreateProductCategory_ResourceAlreadyExistsException() throws JsonProcessingException, Exception {
    ProductCategoryDto category = new ProductCategoryDto(1L, "Pets", new Float("0.2"));
    when(service.addCategory(eq(category))).thenThrow(ResourceAlreadyExistsException.class);

    mvc.perform(post("/categories").accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(category))).andExpect(status().isConflict())
        .andExpect(jsonPath("$.code", is("E001")));

    verify(service).addCategory(eq(category));
  }

  @Test
  public void testFindProductCategory() throws Exception {
    ProductCategoryDto category = new ProductCategoryDto(1L, "Pets", new Float("0.2"));
    when(service.findCategoryById(eq(category.getCode()))).thenReturn(category);

    mvc.perform(post("/categories/1")).andExpect(status().isOk()).andExpect(jsonPath("$.code", is(1)))
        .andExpect(jsonPath("$.description", is(category.getDescription())))
        .andExpect(jsonPath("$.vatRating", is(0.2)));

    verify(service).findCategoryById(eq(category.getCode()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testFindProductCategory_ResourceNotFoundException() throws Exception {
    Long categoryCode = 999L;
    when(service.findCategoryById(eq(categoryCode))).thenThrow(ResourceNotFoundException.class);
    mvc.perform(post("/categories/999")).andExpect(status().isNotFound()).andExpect(jsonPath("$.code", is("E002")));
    verify(service).findCategoryById(eq(categoryCode));
  }

  @Test
  public void testUpdateCategory() throws Exception {
    ProductCategoryDto category = new ProductCategoryDto(1L, "Pets", new Float("0.2"));
    when(service.updateCategory(eq(category))).thenReturn(category);

    mvc.perform(put("/categories/1").accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(category))).andExpect(status().isOk())
        .andExpect(jsonPath("$.code", is(1))).andExpect(jsonPath("$.description", is(category.getDescription())))
        .andExpect(jsonPath("$.vatRating", is(0.2)));

    verify(service).updateCategory(eq(category));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateCategory_ResourceNotFoundException() throws JsonProcessingException, Exception {
    ProductCategoryDto category = new ProductCategoryDto(1L, "Pets", new Float("0.2"));
    when(service.updateCategory(eq(category))).thenThrow(ResourceNotFoundException.class);

    mvc.perform(put("/categories/1").accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
        .content(objectMapper.writeValueAsString(category))).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", is("E002")));

    verify(service).updateCategory(eq(category));
  }

  @Test
  public void testDeleteCategory() throws Exception {
    Long categoryCode = 1L;
    mvc.perform(delete("/categories/1")).andExpect(status().isNoContent());
    verify(service).deleteCategory(eq(categoryCode));
  }

  @Test
  public void testDeleteCategory_ResourceNotFoundException() throws Exception {
    Long categoryCode = 1L;
    doThrow(ResourceNotFoundException.class).when(service).deleteCategory(eq(categoryCode));
    mvc.perform(delete("/categories/1")).andExpect(status().isNotFound()).andExpect(jsonPath("$.code", is("E002")));
    verify(service).deleteCategory(eq(categoryCode));
  }

  @Test
  public void testDeleteCategory_ProductCategoryHasProductsException() throws Exception {
    Long categoryCode = 1L;
    doThrow(ProductCategoryHasProductsException.class).when(service).deleteCategory(eq(categoryCode));
    mvc.perform(delete("/categories/1")).andExpect(status().isConflict()).andExpect(jsonPath("$.code", is("E003")));
    verify(service).deleteCategory(eq(categoryCode));
  }

  @Test
  public void testGetProductsInCategory() throws Exception {
    ProductCategoryDto category = new ProductCategoryDto(1L, "Pets", new Float("0.2"));
    ProductDto parent = new ProductDto(1L, null, category, "Parent Product", new BigDecimal("10.000"), "Blue", "10x10",
        "Parent Product Description");
    ProductDto child = new ProductDto(2L, parent, category, "Child Product", new BigDecimal("15.000"), "Red", "20x20",
        "Child Product Description");
    Collection<ProductDto> products = Arrays.asList(parent, child);
    when(service.findAllProductsInCategory(eq(1L), eq(0), eq(15))).thenReturn(products);

    mvc.perform(get("/categories/1/products").accept(APPLICATION_JSON_UTF8)).andExpect(status().isOk())
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
    verify(service).findAllProductsInCategory(eq(1L), eq(0), eq(15));
  }

}
