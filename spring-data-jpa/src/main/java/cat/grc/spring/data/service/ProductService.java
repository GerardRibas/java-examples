package cat.grc.spring.data.service;

import java.util.Collection;

import cat.grc.spring.data.dto.ProductCategoryDto;
import cat.grc.spring.data.dto.ProductDto;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface ProductService {

  Collection<ProductCategoryDto> findAllCategories(int page, int size);

  ProductCategoryDto findCategoryById(Long id);

  ProductCategoryDto addCategory(ProductCategoryDto category);

  ProductCategoryDto updateCategory(ProductCategoryDto category);

  void deleteCategory(Long id);

  Collection<ProductDto> findAllProducts(int page, int size);

  Collection<ProductDto> findAllProductsInCategory(Long categoryId, int page, int size);

  ProductDto findProductById(Long id);

  ProductDto addProduct(ProductDto product);

  ProductDto updateProduct(ProductDto product);

  void deleteProduct(Long id);

}
