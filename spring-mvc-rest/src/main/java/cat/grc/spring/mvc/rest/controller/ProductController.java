/**
 * 
 */
package cat.grc.spring.mvc.rest.controller;

import java.util.Collection;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
  @ResponseStatus(HttpStatus.CREATED)
  public ProductDto createProduct(@RequestBody ProductDto product) {
    LOGGER.debug("Creating product={}", product);
    return service.addProduct(product);
  }

  @Resource
  public void setService(ProductService service) {
    this.service = service;
  }

}
