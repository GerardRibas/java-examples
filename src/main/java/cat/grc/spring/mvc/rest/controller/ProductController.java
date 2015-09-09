/**
 * 
 */
package cat.grc.spring.mvc.rest.controller;

import java.util.Collection;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import cat.grc.spring.data.dto.ProductDto;
import cat.grc.spring.data.service.ProductService;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RestController
@RequestMapping("/products")
public class ProductController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

  private ProductService service;

  @RequestMapping
  public Collection<ProductDto> getProducts(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "15") int size) {
    LOGGER.debug("Finding products in page={} with size={}", page, size);
    return service.findAllProducts(page, size);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto product, UriComponentsBuilder builder) {
    LOGGER.debug("Creating product={}", product);    
    ProductDto productSaved = service.addProduct(product);
    UriComponents uriComponents = builder.path("/products/{id}").buildAndExpand(productSaved.getId());
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<ProductDto>(productSaved, headers, HttpStatus.CREATED);
  }
    
  @RequestMapping(value = "/{id}")
  public ProductDto findProduct(@PathVariable Long id) {
	  LOGGER.debug("Finding product by id={}", id);
	  return service.findProductById(id);
  }
  
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ProductDto updateProduct(@PathVariable Long id, @RequestBody ProductDto product) {
	  LOGGER.debug("Updating product={} id={}", product, id);
	  return service.updateProduct(product);
  }
  
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public void updateProduct(@PathVariable Long id) {
	  LOGGER.debug("Deleting product by given id={}", id);
	  service.deleteProduct(id);
  }

  @Resource
  public void setService(ProductService service) {
    this.service = service;
  }

}
