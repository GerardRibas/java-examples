/**
 * 
 */
package cat.grc.spring.data.service;

import cat.grc.spring.data.entity.Product;
import cat.grc.spring.data.exception.ResourceNotFoundException;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
interface ProductServicePkg extends ProductService {

  /**
   * Find a product with the given id
   * 
   * @param id of the product
   * @return the product
   * @throws ResourceNotFoundException if the product doesn't exists
   */
  Product findProductEntityById(Long id);

}
