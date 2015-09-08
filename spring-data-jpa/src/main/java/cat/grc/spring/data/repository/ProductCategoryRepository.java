package cat.grc.spring.data.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import cat.grc.spring.data.entity.ProductCategory;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface ProductCategoryRepository extends PagingAndSortingRepository<ProductCategory, Long> {

}
