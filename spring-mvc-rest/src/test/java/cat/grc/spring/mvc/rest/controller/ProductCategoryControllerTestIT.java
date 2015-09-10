package cat.grc.spring.mvc.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cat.grc.spring.data.dto.ProductCategoryDto;
import cat.grc.spring.data.dto.ProductDto;
import cat.grc.spring.mvc.rest.Application;
import cat.grc.spring.mvc.rest.ErrorResource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class ProductCategoryControllerTestIT {

  private static final String CONTROLLER_URL = "http://localhost:8080/categories";

  private RestTemplate restTemplate;

  private ObjectMapper objectMapper;

  @Before
  public void before() {
    objectMapper = new ObjectMapper();
    restTemplate = new RestTemplate();
  }

  @Test
  public void testGetProducts() {
    ResponseEntity<ProductCategoryDto[]> responseEntity =
        restTemplate.getForEntity(CONTROLLER_URL, ProductCategoryDto[].class);
    ProductCategoryDto[] categories = responseEntity.getBody();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    assertEquals("Expected at 15 categories", 15, categories.length);
  }

  @Test
  public void testCreateProductCategory() {
    ProductCategoryDto category = new ProductCategoryDto(null, "Cameras & Optics", new Float("0.2"));
    ResponseEntity<ProductCategoryDto> response =
        restTemplate.postForEntity(CONTROLLER_URL, category, ProductCategoryDto.class);
    assertEquals("Expected Response Status Created", HttpStatus.CREATED, response.getStatusCode());
    assertNotNull("Response id must be populated for saved categories", response.getBody().getCode());
  }

  @Test
  public void testCreateProductCategory_ResourceAlreadyExistsException()
      throws JsonParseException, JsonMappingException, IOException {
    ProductCategoryDto category = new ProductCategoryDto(6L, "Cameras & Optics", new Float("0.2"));
    try {
      restTemplate.postForObject(CONTROLLER_URL, category, ErrorResource.class);
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E001", error.getCode());
      assertEquals("Expected the same message", "CategoryCode=6 already exists", error.getMessage());
    }
  }

  @Test
  public void testGetProductCategory() {
    ResponseEntity<ProductCategoryDto> responseEntity =
        restTemplate.getForEntity(CONTROLLER_URL + "/1", ProductCategoryDto.class);
    ProductCategoryDto expectedCategory = new ProductCategoryDto(1L, "Animals & Pet Supplies", new Float("0.2"));
    ProductCategoryDto category = responseEntity.getBody();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    assertEquals("Expected the same category", expectedCategory, category);
  }

  @Test
  public void testGetProductCategory_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.getForObject(CONTROLLER_URL + "/99999", ErrorResource.class);
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "No ProductCategory found for id=99999", error.getMessage());
    }
  }

  @Test
  public void testUpdateProductCategory() {
    ProductCategoryDto category = new ProductCategoryDto(8L, "Animals & Pet Supplies", new Float("0.2"));
    restTemplate.put(CONTROLLER_URL + "/8", category);
    ResponseEntity<ProductCategoryDto> responseEntity =
        restTemplate.getForEntity(CONTROLLER_URL + "/8", ProductCategoryDto.class);
    ProductCategoryDto updatedCategory = responseEntity.getBody();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    assertEquals("Expected the same category", category, updatedCategory);
  }

  @Test
  public void testUpdateProductCategory_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    ProductCategoryDto category = new ProductCategoryDto(9999L, "Animals & Pet Supplies", new Float("0.2"));
    try {
      restTemplate.put(CONTROLLER_URL + "/99999", category);
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "CategoryCode=9999 not found", error.getMessage());
    }
  }

  @Test
  public void testDeleteCategory() throws JsonParseException, JsonMappingException, IOException {
    restTemplate.delete(CONTROLLER_URL + "/21");
    try {
      restTemplate.getForEntity(CONTROLLER_URL + "/21", ProductDto.class);
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "No ProductCategory found for id=21", error.getMessage());
    }
  }

  @Test
  public void testDeleteCategory_ResourceNotFoundException()
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.delete(CONTROLLER_URL + "/9999");
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E002", error.getCode());
      assertEquals("Expected the same message", "No ProductCategory found for id=9999", error.getMessage());
    }
  }

  @Test
  public void testDeleteCategory_ProductCategoryHasProductsException()
      throws JsonParseException, JsonMappingException, IOException {
    try {
      restTemplate.delete(CONTROLLER_URL + "/1");
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
      ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
      assertEquals("Expected the same error code", "E003", error.getCode());
      assertEquals("Expected the same message", "Product category 1 has products associated", error.getMessage());
    }
  }

  @Test
  public void testGetProductsInCategory() throws Exception {
    ResponseEntity<ProductDto[]> responseEntity =
        restTemplate.getForEntity(CONTROLLER_URL + "/1/products", ProductDto[].class);
    ProductDto[] categories = responseEntity.getBody();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    assertEquals("Expected at 6 products", 6, categories.length);
  }


}
