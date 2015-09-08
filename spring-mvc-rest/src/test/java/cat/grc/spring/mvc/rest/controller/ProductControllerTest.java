/**
 * 
 */
package cat.grc.spring.mvc.rest.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cat.grc.spring.data.dto.ProductCategoryDto;
import cat.grc.spring.data.dto.ProductDto;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
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

  private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

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
    mvc = MockMvcBuilders.standaloneSetup(controller).setHandlerExceptionResolvers(createExceptionResolver()).build();
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

    mvc.perform(MockMvcRequestBuilders.get("/products").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
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

    mvc.perform(MockMvcRequestBuilders.post("/products").accept(APPLICATION_JSON_UTF8)
        .contentType(APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(product)))
        .andExpect(status().isCreated()).andExpect(content().contentType(APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is(productSaved.getId().intValue())))
        .andExpect(jsonPath("$.category.code", is(productSaved.getCategory().getCode().intValue())))
        .andExpect(jsonPath("$.category.description", is(productSaved.getCategory().getDescription())))
        .andExpect(jsonPath("$.category.vatRating", is(0.2))).andExpect(jsonPath("$.name", is(productSaved.getName())))
        .andExpect(jsonPath("$.price", is(10.0))).andExpect(jsonPath("$.color", is(productSaved.getColor())))
        .andExpect(jsonPath("$.size", is(productSaved.getSize())))
        .andExpect(jsonPath("$.description", is(productSaved.getDescription())));

    verify(service).addProduct(eq(product));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testCreateProduct_ResourceAlreadyExistsException() throws JsonProcessingException, Exception {
    ProductCategoryDto category = new ProductCategoryDto(1L, "Pets", new Float("0.2"));
    ProductDto product =
        new ProductDto(1L, null, category, "Product", new BigDecimal("10.000"), "Blue", "10x10", "Product Description");

    when(service.addProduct(Mockito.any(ProductDto.class))).thenThrow(ResourceAlreadyExistsException.class);

    mvc.perform(MockMvcRequestBuilders.post("/products").accept(APPLICATION_JSON_UTF8)
        .contentType(APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(product)))
        .andExpect(status().isConflict()).andExpect(jsonPath("$.code", is("E001")));

    verify(service).addProduct(eq(product));
  }

  private ExceptionHandlerExceptionResolver createExceptionResolver() {
    ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
      @Override
      protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod,
          Exception exception) {
        Method method = new ExceptionHandlerMethodResolver(ExceptionHandlerController.class).resolveMethod(exception);
        return new ServletInvocableHandlerMethod(new ExceptionHandlerController(), method);
      }
    };
    exceptionResolver.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    exceptionResolver.afterPropertiesSet();
    return exceptionResolver;
  }

}
