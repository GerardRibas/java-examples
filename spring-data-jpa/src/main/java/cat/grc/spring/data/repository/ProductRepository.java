/**
 * 
 */
package cat.grc.spring.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cat.grc.spring.data.entity.Product;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {


  @Query("select p from Product p where p.category.code = ?1")
  Page<Product> findByCategory(Long category, Pageable pageable);

}
