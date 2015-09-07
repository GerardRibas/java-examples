package cat.grc.spring.data.service;

import java.util.Collection;

import cat.grc.spring.data.dto.ProductCategoryDto;
import cat.grc.spring.data.dto.ProductDto;
import cat.grc.spring.data.exception.ProductCategoryHasProductsException;
import cat.grc.spring.data.exception.ResourceAlreadyExistsException;
import cat.grc.spring.data.exception.ResourceNotFoundException;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface ProductService {

  /**
   * Find all product categories
   * 
   * @param page the page requested
   * @param size the size of the page
   * @return the product categores
   */
  Collection<ProductCategoryDto> findAllCategories(int page, int size);

  /**
   * Find a product category with the given id
   * 
   * @param id of the product category
   * @return the product category
   * @throws ResourceNotFoundException if the id of the product doesn't exists
   */
  ProductCategoryDto findCategoryById(Long id);

  /**
   * Add a new category
   * 
   * @param category the category to be added
   * @return the new category saved
   * @throws ResourceAlreadyExistsException if the product category was saved previously
   */
  ProductCategoryDto addCategory(ProductCategoryDto category);

  /**
   * Update a category
   * 
   * @param category the product category to be updated
   * @return the product category to be updated
   * @throws ResourceNotFoundException if the category doesn't exists
   */
  ProductCategoryDto updateCategory(ProductCategoryDto category);

  /**
   * Delete a product category
   * 
   * @param id of the product category
   * @throws ProductCategoryHasProductsException if products has this category
   * @throws ResourceNotFoundException if the Product Category doesn't exists
   */
  void deleteCategory(Long id);

  /**
   * Find all products
   * 
   * @param page the page requested
   * @param size the size of the page
   * @return the products
   */
  Collection<ProductDto> findAllProducts(int page, int size);

  /**
   * Find all products in a category
   * 
   * @param categoryId the category
   * @param page the page requested
   * @param size the size of the page
   * @return the products
   */
  Collection<ProductDto> findAllProductsInCategory(Long categoryId, int page, int size);

  /**
   * Find product with given id
   * 
   * @param id the id of the product
   * @return the Product
   * @throws ResourceNotFoundException if the product doesn't exists
   */
  ProductDto findProductById(Long id);

  /**
   * Add a new product
   * 
   * @param product the product to add
   * @return the product saved
   * @throws ResourceAlreadyExistsException if the product was saved previously
   */
  ProductDto addProduct(ProductDto product);

  /**
   * Update a product
   * 
   * @param product to be updated
   * @return the product update
   * @throws ResourceNotFoundException if the product doesn't exists
   */
  ProductDto updateProduct(ProductDto product);

  /**
   * Delete a product
   * 
   * @param id of the product to be deleted
   * @throws ResourceNotFoundException if the product doesn't exists
   */
  void deleteProduct(Long id);

}
