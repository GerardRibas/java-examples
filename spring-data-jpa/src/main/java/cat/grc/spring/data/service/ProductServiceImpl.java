/**
 * 
 */
package cat.grc.spring.data.service;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import cat.grc.spring.data.dto.ProductCategoryDto;
import cat.grc.spring.data.dto.ProductDto;
import cat.grc.spring.data.entity.Product;
import cat.grc.spring.data.entity.ProductCategory;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;
import cat.grc.spring.data.mapper.ProductCategoryMapper;
import cat.grc.spring.data.mapper.ProductMapper;
import cat.grc.spring.data.repository.ProductCategoryRepository;
import cat.grc.spring.data.repository.ProductRepository;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@Service
public class ProductServiceImpl implements ProductService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

  private ProductCategoryRepository productCategoryRepository;

  private ProductRepository productRepository;

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.ProductService#findAllCategories(int, int)
   */
  @Override
  @Transactional(readOnly = true)
  public Collection<ProductCategoryDto> findAllCategories(int page, int size) {
    LOGGER.debug("Finding product categories by page={} and size={}", page, size);
    Pageable pageable = new PageRequest(page, size);
    Page<ProductCategory> productCategoryPage = productCategoryRepository.findAll(pageable);
    return productCategoryPage.getContent().stream().map(category -> ProductCategoryMapper.toDto(category))
        .collect(Collectors.toList());
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.ProductService#findCategoryById(java.lang.Long)
   */
  @Override
  @Transactional(readOnly = true)
  public ProductCategoryDto findCategoryById(Long id) {
    LOGGER.debug("Finding category by id={}", id);
    Assert.notNull(id);
    ProductCategory entity = productCategoryRepository.findOne(id);
    if (entity == null) {
      String msg = String.format("No ProductCategory found for id=%d", id);
      LOGGER.warn(msg);
      throw new ResourceNotFoundException(msg);
    }
    return ProductCategoryMapper.toDto(entity);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.ProductService#addCategory(cat.grc.spring.data.dto.
   * ProductCategoryDto)
   */
  @Override
  @Transactional
  public ProductCategoryDto addCategory(ProductCategoryDto category) {
    LOGGER.debug("Adding new product category {}", category);
    Assert.notNull(category);
    boolean exists = category.getCode() == null ? false : productCategoryRepository.exists(category.getCode());
    if (exists) {
      String msg = String.format("CategoryCode=%s already exists", category.getCode());
      LOGGER.warn(msg);
      throw new ResourceAlreadyExistsException(msg);
    }
    ProductCategory entity = productCategoryRepository.save(ProductCategoryMapper.toEntity(category));
    return ProductCategoryMapper.toDto(entity);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.ProductService#updateCategory(cat.grc.spring.data.dto.
   * ProductCategoryDto)
   */
  @Override
  @Transactional
  public ProductCategoryDto updateCategory(ProductCategoryDto category) {
    LOGGER.debug("Updating product category {}", category);
    Assert.notNull(category);
    productCategoryMustExists(category.getCode());
    ProductCategory entity = productCategoryRepository.save(ProductCategoryMapper.toEntity(category));
    return ProductCategoryMapper.toDto(entity);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.ProductService#deleteCategory(java.lang.Long)
   */
  @Override
  @Transactional
  public void deleteCategory(Long id) {
    LOGGER.debug("Deleting product category by id={}", id);
    productCategoryMustExists(id);
    productCategoryRepository.delete(id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.ProductService#findAllProducts(int, int)
   */
  @Override
  @Transactional(readOnly = true)
  public Collection<ProductDto> findAllProducts(int page, int size) {
    LOGGER.debug("Finding products by page={} and size={}", page, size);
    Pageable pageable = new PageRequest(page, size);
    Page<Product> productPage = productRepository.findAll(pageable);
    return productPage.getContent().stream().map(category -> ProductMapper.toDto(category))
        .collect(Collectors.toList());
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.ProductService#findAllProductsInCategory(java.lang.Long, int,
   * int)
   */
  @Override
  @Transactional(readOnly = true)
  public Collection<ProductDto> findAllProductsInCategory(Long categoryId, int page, int size) {
    LOGGER.debug("Finding products by page={} and size={}", page, size);
    Pageable pageable = new PageRequest(page, size);
    Page<Product> productPage = productRepository.findByCategory(categoryId, pageable);
    return productPage.getContent().stream().map(category -> ProductMapper.toDto(category))
        .collect(Collectors.toList());
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.ProductService#findProductById(java.lang.Long)
   */
  @Override
  @Transactional(readOnly = true)
  public ProductDto findProductById(Long id) {
    LOGGER.debug("Finding product by id={}", id);
    Assert.notNull(id);
    Product entity = productRepository.findOne(id);
    if (entity == null) {
      String msg = String.format("No Product found for id=%d", id);
      LOGGER.warn(msg);
      throw new ResourceNotFoundException(msg);
    }
    return ProductMapper.toDto(entity);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.ProductService#addProduct(cat.grc.spring.data.dto.ProductDto)
   */
  @Override
  @Transactional
  public ProductDto addProduct(ProductDto product) {
    LOGGER.debug("Adding new product {}", product);
    Assert.notNull(product);
    boolean exists = product.getId() == null ? false : productRepository.exists(product.getId());
    if (exists) {
      String msg = String.format("ProductId=%s already exists", product.getId());
      LOGGER.warn(msg);
      throw new ResourceAlreadyExistsException(msg);
    }
    Product entity = productRepository.save(ProductMapper.toEntity(product));
    return ProductMapper.toDto(entity);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cat.grc.spring.data.service.ProductService#updateProduct(cat.grc.spring.data.dto.ProductDto)
   */
  @Override
  @Transactional
  public ProductDto updateProduct(ProductDto product) {
    LOGGER.debug("Updating product {}", product);
    Assert.notNull(product);
    productMustExists(product.getId());
    Product entity = productRepository.save(ProductMapper.toEntity(product));
    return ProductMapper.toDto(entity);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cat.grc.spring.data.service.ProductService#deleteProduct(java.lang.Long)
   */
  @Override
  @Transactional
  public void deleteProduct(Long id) {
    LOGGER.debug("Deleting product by id={}", id);
    productMustExists(id);
    productRepository.delete(id);
  }

  private boolean productCategoryMustExists(Long id) {
    Assert.notNull(id);
    boolean result = productCategoryRepository.exists(id);
    if (!result) {
      String msg = String.format("CategoryCode=%s not found", id);
      LOGGER.warn(msg);
      throw new ResourceNotFoundException(msg);
    }
    return result;
  }

  private boolean productMustExists(Long id) {
    Assert.notNull(id);
    boolean result = productRepository.exists(id);
    if (!result) {
      String msg = String.format("Product=%s not found", id);
      LOGGER.warn(msg);
      throw new ResourceNotFoundException(msg);
    }
    return result;
  }

  @Resource
  public void setProductCategoryRepository(ProductCategoryRepository productCategoryRepository) {
    this.productCategoryRepository = productCategoryRepository;
  }

  @Resource
  public void setProductRepository(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

}
