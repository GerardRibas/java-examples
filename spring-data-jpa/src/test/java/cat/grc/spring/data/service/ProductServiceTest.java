/**
 * 
 */
package cat.grc.spring.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import cat.grc.spring.data.dto.ProductCategoryDto;
import cat.grc.spring.data.dto.ProductDto;
import cat.grc.spring.data.entity.Product;
import cat.grc.spring.data.entity.ProductCategory;
import cat.grc.spring.data.exception.ProductCategoryHasProductsException;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.repository.ProductCategoryRepository;
import cat.grc.spring.data.repository.ProductRepository;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class ProductServiceTest {

  private ProductServiceImpl service;

  @Mock
  private ProductCategoryRepository productCategoryRepository;

  @Mock
  private ProductRepository productRepository;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    service = new ProductServiceImpl();
    service.setProductCategoryRepository(productCategoryRepository);
    service.setProductRepository(productRepository);
    service.setModelMapper(new ModelMapper());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindAllCategories() {
    int page = 1;
    int size = 15;
    Pageable pageable = new PageRequest(page, size);
    Page<ProductCategory> pageCategory = mock(Page.class);
    ProductCategory result = mock(ProductCategory.class);
    when(productCategoryRepository.findAll(eq(pageable))).thenReturn(pageCategory);
    when(pageCategory.getContent()).thenReturn(Arrays.asList(result));

    Collection<ProductCategoryDto> categories = service.findAllCategories(page, size);
    assertFalse("Expected a non empty collection", categories.isEmpty());
    assertEquals("Expected only one result", 1, categories.size());

    verify(productCategoryRepository).findAll(eq(pageable));
    verifyNoMoreInteractions(productCategoryRepository);
    verifyZeroInteractions(productRepository);
  }

  @Test
  public void testFindCategoryById() {
    Long categoryId = 1L;
    ProductCategory result = mock(ProductCategory.class);
    when(productCategoryRepository.findOne(eq(categoryId))).thenReturn(result);

    ProductCategoryDto category = service.findCategoryById(categoryId);
    assertNotNull("Expected a Product Category", category);

    verify(productCategoryRepository).findOne(eq(categoryId));
    verifyNoMoreInteractions(productCategoryRepository);
    verifyZeroInteractions(productRepository);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testFindCategoryById_ResourceNotFoundException() {
    Long categoryId = 1L;
    when(productCategoryRepository.findOne(eq(categoryId))).thenReturn(null);
    service.findCategoryById(categoryId);
  }

  @Test
  public void testAddCategory() {
    ProductCategoryDto dto = new ProductCategoryDto(null, "Arts & Entertainment", new Float("0.2"));
    ProductCategory entityToSave = new ProductCategory(null, dto.getDescription(), dto.getVatRating());
    ProductCategory entitySaved = new ProductCategory(1L, dto.getDescription(), dto.getVatRating());
    when(productCategoryRepository.save(eq(entityToSave))).thenReturn(entitySaved);
    ProductCategoryDto dtoSaved = service.addCategory(dto);
    assertNotNull("Expected a dto", dtoSaved);
    assertEquals("Code is not equals", entitySaved.getCode(), dtoSaved.getCode());
    assertEquals("Description is not equals", entitySaved.getDescription(), dtoSaved.getDescription());
    assertEquals("VatRating is not equals", entitySaved.getVatRating(), dtoSaved.getVatRating());

    verify(productCategoryRepository).save(eq(entityToSave));
    verify(productCategoryRepository, new Times(0)).exists(anyLong());
    verifyNoMoreInteractions(productCategoryRepository);
    verifyZeroInteractions(productRepository);
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testAddCategory_ResourceAlreadyExistsException() {
    ProductCategoryDto dto = new ProductCategoryDto(1L, "Arts & Entertainment", new Float("0.2"));
    when(productCategoryRepository.exists(eq(dto.getCode()))).thenReturn(true);
    service.addCategory(dto);
  }

  @Test
  public void testUpdateCategory() {
    ProductCategoryDto dto = new ProductCategoryDto(1L, "Arts & Entertainment", new Float("0.2"));
    ProductCategory entityToUpdate = new ProductCategory(dto.getCode(), dto.getDescription(), dto.getVatRating());
    when(productCategoryRepository.exists(eq(dto.getCode()))).thenReturn(true);
    when(productCategoryRepository.save(eq(entityToUpdate))).thenReturn(entityToUpdate);

    ProductCategoryDto dtoUpdated = service.updateCategory(dto);
    assertEquals("Expected the same dto", dto, dtoUpdated);

    verify(productCategoryRepository).save(eq(entityToUpdate));
    verify(productCategoryRepository).exists(eq(dto.getCode()));
    verifyNoMoreInteractions(productCategoryRepository);
    verifyZeroInteractions(productRepository);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testUpdateCategory_ResourceNotFoundException() {
    ProductCategoryDto dto = new ProductCategoryDto(1L, "Arts & Entertainment", new Float("0.2"));
    when(productCategoryRepository.exists(eq(dto.getCode()))).thenReturn(false);
    service.updateCategory(dto);
  }

  @Test
  public void testDeleteCategory() {
    Long categoryId = 1L;
    ProductCategory productCategory = mock(ProductCategory.class);
    when(productCategoryRepository.findOne(eq(categoryId))).thenReturn(productCategory);
    service.deleteCategory(categoryId);
    verify(productCategoryRepository).delete(eq(categoryId));
    verify(productCategoryRepository).findOne(eq(categoryId));
    verifyNoMoreInteractions(productCategoryRepository);
    verifyZeroInteractions(productRepository);
  }

  @Test(expected = ProductCategoryHasProductsException.class)
  public void testDeleteCategory_ProductCategoryHasProductsException() {
    Long categoryId = 1L;
    ProductCategory productCategory = mock(ProductCategory.class);
    Product product = mock(Product.class);
    when(productCategoryRepository.findOne(eq(categoryId))).thenReturn(productCategory);
    when(productCategory.getProducts()).thenReturn(Arrays.asList(product));
    service.deleteCategory(categoryId);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testDeleteCategory_ResourceNotFoundException() {
    Long categoryId = 1L;
    when(productCategoryRepository.exists(eq(categoryId))).thenReturn(false);
    service.deleteCategory(categoryId);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindAllProducts() {
    int page = 1;
    int size = 15;
    Pageable pageable = new PageRequest(page, size);
    Page<Product> pageCategory = mock(Page.class);
    Product result = mock(Product.class);
    when(productRepository.findAll(eq(pageable))).thenReturn(pageCategory);
    when(pageCategory.getContent()).thenReturn(Arrays.asList(result));

    Collection<ProductDto> categories = service.findAllProducts(page, size);
    assertFalse("Expected a non empty collection", categories.isEmpty());
    assertEquals("Expected only one result", 1, categories.size());

    verify(productRepository).findAll(eq(pageable));
    verifyNoMoreInteractions(productRepository);
    verifyZeroInteractions(productCategoryRepository);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFindAllProductsInCategory() {
    Long categoryId = 1L;
    int page = 1;
    int size = 15;
    Pageable pageable = new PageRequest(page, size);
    Page<Product> pageCategory = mock(Page.class);
    Product result = mock(Product.class);
    when(productRepository.findByCategory(eq(categoryId), eq(pageable))).thenReturn(pageCategory);
    when(pageCategory.getContent()).thenReturn(Arrays.asList(result));

    Collection<ProductDto> categories = service.findAllProductsInCategory(categoryId, page, size);
    assertFalse("Expected a non empty collection", categories.isEmpty());
    assertEquals("Expected only one result", 1, categories.size());

    verify(productRepository).findByCategory(eq(categoryId), eq(pageable));
    verifyNoMoreInteractions(productRepository);
    verifyZeroInteractions(productCategoryRepository);
  }

  @Test
  public void testFindProductById() {
    Long productId = 1L;
    Product result = mock(Product.class);
    when(productRepository.findOne(eq(productId))).thenReturn(result);

    ProductDto category = service.findProductById(productId);
    assertNotNull("Expected a Product", category);

    verify(productRepository).findOne(eq(productId));
    verifyNoMoreInteractions(productRepository);
    verifyZeroInteractions(productCategoryRepository);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testFindProductById_ResourceNotFoundException() {
    Long productId = 1L;
    when(productRepository.findOne(eq(productId))).thenReturn(null);

    service.findProductById(productId);
  }

  @Test
  public void testAddProduct() {
    ProductDto dto = new ProductDto(null, null, null, "Some Product Name", new BigDecimal("10.22"), "Blue", "10x10",
        "Some Description");
    Product entityToSave = new Product(dto.getId(), null, null, dto.getName(), dto.getPrice(), dto.getColor(),
        dto.getSize(), dto.getDescription());
    Product entitySaved =
        new Product(1L, null, null, dto.getName(), dto.getPrice(), dto.getColor(), dto.getSize(), dto.getDescription());

    when(productRepository.save(eq(entityToSave))).thenReturn(entitySaved);
    ProductDto dtoSaved = service.addProduct(dto);
    assertNotNull("Expected a dto", dtoSaved);
    assertEquals("ProductId not equals", entitySaved.getId(), dtoSaved.getId());
    assertEquals("Name not equals", dto.getName(), dtoSaved.getName());
    assertEquals("Price not equals", dto.getPrice(), dtoSaved.getPrice());
    assertEquals("Color not equals", dto.getColor(), dtoSaved.getColor());
    assertEquals("Size not equals", dto.getSize(), dtoSaved.getSize());
    assertEquals("Description not equals", dto.getDescription(), dtoSaved.getDescription());

    verify(productRepository).save(eq(entityToSave));
    verify(productRepository, new Times(0)).exists(anyLong());
    verifyNoMoreInteractions(productRepository);
    verifyZeroInteractions(productCategoryRepository);
  }

  @Test(expected = ResourceAlreadyExistsException.class)
  public void testAddProduct_ResourceAlreadyExistsException() {
    ProductDto dto = new ProductDto(1L, null, null, "Some Product Name", new BigDecimal("10.22"), "Blue", "10x10",
        "Some Description");
    when(productRepository.exists(eq(dto.getId()))).thenReturn(true);
    service.addProduct(dto);
  }

  @Test
  public void testUpdateProduct() {
    ProductDto dto = new ProductDto(1L, null, null, "Some Product Name", new BigDecimal("10.22"), "Blue", "10x10",
        "Some Description");
    Product entityToUpdate = new Product(dto.getId(), null, null, dto.getName(), dto.getPrice(), dto.getColor(),
        dto.getSize(), dto.getDescription());

    when(productRepository.exists(eq(dto.getId()))).thenReturn(true);
    when(productRepository.save(eq(entityToUpdate))).thenReturn(entityToUpdate);
    ProductDto dtoUpdated = service.updateProduct(dto);
    assertNotNull("Expected a dto", dtoUpdated);
    assertEquals("ProductId not equals", entityToUpdate.getId(), dtoUpdated.getId());
    assertEquals("Name not equals", dto.getName(), dtoUpdated.getName());
    assertEquals("Price not equals", dto.getPrice(), dtoUpdated.getPrice());
    assertEquals("Color not equals", dto.getColor(), dtoUpdated.getColor());
    assertEquals("Size not equals", dto.getSize(), dtoUpdated.getSize());
    assertEquals("Description not equals", dto.getDescription(), dtoUpdated.getDescription());

    verify(productRepository).save(eq(entityToUpdate));
    verify(productRepository).exists(anyLong());
    verifyNoMoreInteractions(productRepository);
    verifyZeroInteractions(productCategoryRepository);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testUpdateProduct_ResourceNotFoundException() {
    ProductDto dto = new ProductDto(1L, null, null, "Some Product Name", new BigDecimal("10.22"), "Blue", "10x10",
        "Some Description");
    when(productRepository.exists(eq(dto.getId()))).thenReturn(false);
    service.updateProduct(dto);
  }

  @Test
  public void testDeleteProduct() {
    Long productId = 1L;
    when(productRepository.exists(eq(productId))).thenReturn(true);
    service.deleteProduct(productId);
    verify(productRepository).delete(eq(productId));
    verify(productRepository).exists(eq(productId));
    verifyNoMoreInteractions(productRepository);
    verifyZeroInteractions(productCategoryRepository);
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testDeleteProduct_ResourceNotFoundException() {
    Long productId = 1L;
    when(productRepository.exists(eq(productId))).thenReturn(false);
    service.deleteProduct(productId);
  }

}
