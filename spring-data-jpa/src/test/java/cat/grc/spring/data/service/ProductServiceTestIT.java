/**
 * 
 */
package cat.grc.spring.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import cat.grc.spring.data.dto.ProductCategoryDto;
import cat.grc.spring.data.dto.ProductDto;
import cat.grc.spring.data.exception.ProductCategoryHasProductsException;
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
@DatabaseSetup("ProductServiceTestIT_setup.xml")
public class ProductServiceTestIT {

  @Autowired
  private ProductService service;

  @Autowired
  private EntityManager entityManager;

  @Test
  public void testFindAllCategories() {
    Collection<ProductCategoryDto> products = service.findAllCategories(0, 15);
    assertFalse("Expected at least one category", products.isEmpty());
  }

  @Test
  public void testFindCategoryById() {
    Long categoryId = 1L;
    ProductCategoryDto category = service.findCategoryById(categoryId);
    assertNotNull("Expected a category", category);
    ProductCategoryDto expectedCategogry = new ProductCategoryDto(categoryId, "Animals & Pet Supplies", new Float(0.2));
    assertEquals("Expected the same category", expectedCategogry, category);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testFindCategoryById_ResourceNotFoundException() {
    Long categoryId = 99999L;
    service.findCategoryById(categoryId);
  }

  @Test
  @ExpectedDatabase(value = "ProductServiceTestIT.testAddCategory.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testAddCategory() {
    ProductCategoryDto category = new ProductCategoryDto(null, "Apparel & Accessories", new Float(0.2));
    ProductCategoryDto savedCategory = service.addCategory(category);
    assertNotNull("Expected a category returned", savedCategory);
    assertNotNull("Expected a code generated", savedCategory.getCode());
    assertEquals("Expected the same description", category.getDescription(), savedCategory.getDescription());
    assertEquals("Expected the same vat rating", category.getVatRating(), savedCategory.getVatRating());
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testAddCategory_ResourceAlreadyExistsException() {
    ProductCategoryDto category = new ProductCategoryDto(1L, "Apparel & Accessories", new Float(0.2));
    service.addCategory(category);
  }

  @Test
  @ExpectedDatabase(value = "ProductServiceTestIT.testUpdateCategory.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testUpdateCategory() {
    ProductCategoryDto category = new ProductCategoryDto(1L, "Arts & Entertainment", new Float(0.3));
    ProductCategoryDto updatedCategory = service.updateCategory(category);
    assertEquals("Expected the same category", category, updatedCategory);
    entityManager.flush();
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testUpdateCategory_ResourceNotFoundException() {
    ProductCategoryDto category = new ProductCategoryDto(9999L, "Arts & Entertainment", new Float(0.3));
    service.updateCategory(category);
  }

  @Test
  @ExpectedDatabase(value = "ProductServiceTestIT.testDeleteCategory.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testDeleteCategory() {
    service.deleteCategory(2L);
    entityManager.flush();
  }

  @Test(expected = ProductCategoryHasProductsException.class)
  public void testDeleteCategory_ProductCategoryHasProductsException() {
    service.deleteCategory(1L);
  }

  @Test
  public void testFindAllProducts() {
    Collection<ProductDto> products = service.findAllProducts(0, 15);
    assertFalse("At least two products on it", products.isEmpty());
  }

  @Test
  public void testFindAllProductsInCategory() {
    Collection<ProductDto> products = service.findAllProductsInCategory(1L, 0, 15);
    assertFalse("At least one products on it", products.isEmpty());
    assertEquals("One product expected with this category", 1, products.size());
  }

  @Test
  public void testFindProductById() {
    Long productId = 1L;
    ProductDto product = service.findProductById(productId);
    ProductCategoryDto category = new ProductCategoryDto(1L, "Animals & Pet Supplies", new Float("0.2"));
    ProductDto expectedProduct = new ProductDto(1L, null, category, "lacinia. Sed congue, elit sed",
        new BigDecimal("9.458"), "Blue", "10x10", "Lorem ipsum dolor sit amet, consectetuer");
    assertEquals("Expected the same product", expectedProduct, product);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testFindProductById_ResourceNotFoundException() {
    service.findProductById(999999L);
  }

  @Test
  @ExpectedDatabase(value = "ProductServiceTestIT.testAddProduct.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testAddProduct() {
    ProductCategoryDto category = new ProductCategoryDto(1L, "Animals & Pet Supplies", new Float("0.2"));
    ProductDto product = new ProductDto(null, null, category, "Aliquam ornare, libero at auctor ullamcorper, nisl",
        new BigDecimal("19.206"), "Red", "30x10", "Lorem ipsum dolor sit amet, consectetuer adipiscing");
    ProductDto savedProduct = service.addProduct(product);
    assertNotNull(savedProduct);
    assertNotNull(savedProduct.getId());
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testAddProduct_ResourceAlreadyExistsException() {
    ProductCategoryDto category = new ProductCategoryDto(1L, "Animals & Pet Supplies", new Float("0.2"));
    ProductDto product = new ProductDto(1L, null, category, "Aliquam ornare, libero at auctor ullamcorper, nisl",
        new BigDecimal("19.206"), "Red", "30x10", "Lorem ipsum dolor sit amet, consectetuer adipiscing");
    service.addProduct(product);
  }

  @Test
  @ExpectedDatabase(value = "ProductServiceTestIT.testUpdateProduct.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testUpdateProduct() {
    ProductCategoryDto category = new ProductCategoryDto(2L, "Electronics", new Float("0.2"));
    ProductDto product = new ProductDto(1L, null, category, "Updated name", new BigDecimal("20.003"), "Red", "30x30",
        "Updated Description");
    ProductDto updatedProduct = service.updateProduct(product);
    assertEquals("Returned product has to match with given product", product, updatedProduct);
    entityManager.flush();
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testUpdateProduct_ResourceNotFoundException() {
    ProductCategoryDto category = new ProductCategoryDto(2L, "Electronics", new Float("0.2"));
    ProductDto product = new ProductDto(9999L, null, category, "Updated name", new BigDecimal("20.003"), "Red", "30x30",
        "Updated Description");
    service.updateProduct(product);
  }

  @Test
  @ExpectedDatabase(value = "ProductServiceTestIT.testDeleteProduct.expected.xml",
      assertionMode = DatabaseAssertionMode.NON_STRICT)
  public void testDeleteProduct() {
    service.deleteProduct(2L);
    entityManager.flush();
  }

}
