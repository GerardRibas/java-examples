/**
 * 
 */
package cat.grc.spring.mvc.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.math.BigDecimal;

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

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class ProductControllerTestIT {
	
	private static final String CONTROLLER_URL = "http://localhost:8080/products";
	
	private RestTemplate restTemplate;
	
	private ObjectMapper objectMapper;
	
	@Before
	public void before(){
		objectMapper = new ObjectMapper();
		restTemplate = new RestTemplate();
	}
	
	@Test
	public void testGetProducts() {
		ResponseEntity<ProductDto[]> responseEntity = restTemplate.getForEntity(CONTROLLER_URL, ProductDto[].class);
		ProductDto[] products = responseEntity.getBody();
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		assertEquals("Expected at 15 products", 15, products.length);
	}
	
	@Test
	public void testGetProductsPageAndSizeNoDefault() {
		ResponseEntity<ProductDto[]> responseEntity = restTemplate.getForEntity(CONTROLLER_URL+ "?page=1&size=3", ProductDto[].class);
		ProductDto[] products = responseEntity.getBody();
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		assertEquals("Expected at 3 products", 3, products.length);
	}
	
	@Test
	public void testCreateProduct() {
		ProductCategoryDto category = new ProductCategoryDto(6L, "Cameras & Optics", new Float("0.2"));
		ProductDto product = new ProductDto(null, null, category, "lacinia. Sed congue, elit sed", new BigDecimal("9.458"), null, null, "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Curabitur sed tortor. Integer aliquam adipiscing lacus. Ut nec");
		ResponseEntity<ProductDto> response = restTemplate.postForEntity(CONTROLLER_URL, product, ProductDto.class);
		assertEquals("Expected Response Status Created", HttpStatus.CREATED, response.getStatusCode());
		assertNotNull("Response id must be populated for saved products", response.getBody().getId());
	}
	
	@Test
	public void testCreateProduct_ResourceAlreadyExistsException() throws JsonParseException, JsonMappingException, IOException {
		ProductCategoryDto category = new ProductCategoryDto(6L, "Cameras & Optics", new Float("0.2"));
		ProductDto product = new ProductDto(1L, null, category, "lacinia. Sed congue, elit sed", new BigDecimal("9.458"), null, null, "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Curabitur sed tortor. Integer aliquam adipiscing lacus. Ut nec");		
		try {
			restTemplate.postForObject(CONTROLLER_URL, product, ErrorResource.class);	
		} catch (HttpClientErrorException e) {
			assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
			ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
			assertEquals("Expected the same error code", "E001", error.getCode());
			assertEquals("Expected the same message", "ProductId=1 already exists", error.getMessage());
		}	
	}
	
	@Test
	public void testGetProduct() {
		ResponseEntity<ProductDto> responseEntity = restTemplate.getForEntity("http://localhost:8080/products/1", ProductDto.class);
		ProductCategoryDto category = new ProductCategoryDto(6L, "Cameras & Optics", new Float("0.2"));
		ProductDto expectedProduct = new ProductDto(1L, null, category, "lacinia. Sed congue, elit sed", new BigDecimal("9.458"), null, null, "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Curabitur sed tortor. Integer aliquam adipiscing lacus. Ut nec");		
		ProductDto product = responseEntity.getBody();
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		assertEquals("Expected the same product", expectedProduct, product);
	}
	
	@Test
	public void testGetProduct_ResourceNotFoundException() throws JsonParseException, JsonMappingException, IOException {	
		try {
			restTemplate.getForEntity(CONTROLLER_URL+"/9999", ProductDto.class);
		} catch (HttpClientErrorException e) {
			assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
			ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
			assertEquals("Expected the same error code", "E002", error.getCode());
			assertEquals("Expected the same message", "No Product found for id=9999", error.getMessage());
		}	
	}
	
	@Test
	public void testUpdateProduct() {
		ProductCategoryDto category = new ProductCategoryDto(6L, "Cameras & Optics", new Float("0.2"));
		ProductDto product = new ProductDto(1L, null, category, "Some Product Name", new BigDecimal("10.458"), "Blue", "10x10", "Some Product Description");
		restTemplate.put(CONTROLLER_URL + "/1", product);
		ResponseEntity<ProductDto> savedProduct = restTemplate.getForEntity("http://localhost:8080/products/1", ProductDto.class);
		assertEquals("Expected the same product", product, savedProduct.getBody());
	}
	
	@Test
	public void testUpdateProduct_ResourceNotFoundException() throws JsonParseException, JsonMappingException, IOException {
		ProductCategoryDto category = new ProductCategoryDto(6L, "Cameras & Optics", new Float("0.2"));
		ProductDto product = new ProductDto(9999L, null, category, "lacinia. Sed congue, elit sed", new BigDecimal("9.458"), null, null, "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Curabitur sed tortor. Integer aliquam adipiscing lacus. Ut nec");		
		try {
			restTemplate.put(CONTROLLER_URL+"/9999", product);	
		} catch (HttpClientErrorException e) {
			assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
			ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
			assertEquals("Expected the same error code", "E002", error.getCode());
			assertEquals("Expected the same message", "Product=9999 not found", error.getMessage());
		}	
	}
	
	@Test
	public void testDeleteProduct() throws JsonParseException, JsonMappingException, IOException {
		restTemplate.delete(CONTROLLER_URL+"/55");
		try {
			restTemplate.getForEntity(CONTROLLER_URL+"/55", ProductDto.class);
		} catch (HttpClientErrorException e) {
			assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
			ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
			assertEquals("Expected the same error code", "E002", error.getCode());
			assertEquals("Expected the same message", "No Product found for id=55", error.getMessage());
		}
	}
	
	@Test
	public void testDeleteProduct_ResourceNotFoundException() throws JsonParseException, JsonMappingException, IOException {
		try {
			restTemplate.delete(CONTROLLER_URL+"/9999");
		} catch (HttpClientErrorException e) {
			assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
			ErrorResource error = objectMapper.readValue(e.getResponseBodyAsByteArray(), ErrorResource.class);
			assertEquals("Expected the same error code", "E002", error.getCode());
			assertEquals("Expected the same message", "Product=9999 not found", error.getMessage());
		}
	}

}

