/**
 * 
 */
package cat.grc.spring.data.service;

import cat.grc.spring.data.entity.Product;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
interface ProductServicePkg extends ProductService {

  Product findProductEntityById(Long id);

}
