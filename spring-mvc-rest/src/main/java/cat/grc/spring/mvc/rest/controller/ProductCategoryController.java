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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import cat.grc.spring.data.dto.ProductCategoryDto;
import cat.grc.spring.data.dto.ProductDto;
import cat.grc.spring.data.service.ProductService;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RequestMapping("/categories")
@RestController
public class ProductCategoryController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductCategoryController.class);

  private ProductService service;

  @RequestMapping
  public Collection<ProductCategoryDto> getProductCategories(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "15") int size) {
    LOGGER.debug("Finding all product categories in page={} with size={}", page, size);
    return service.findAllCategories(page, size);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ProductCategoryDto> createProductCategory(@RequestBody ProductCategoryDto category,
      UriComponentsBuilder builder) {
    LOGGER.debug("Creating Product Category={}", category);
    ProductCategoryDto categorySaved = service.addCategory(category);
    UriComponents uriComponents = builder.path("categories/{id}").buildAndExpand(categorySaved.getCode());
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<ProductCategoryDto>(categorySaved, headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/{id}")
  public ProductCategoryDto findProductCategory(@PathVariable Long id) {
    LOGGER.debug("Finding product category by id={}", id);
    return service.findCategoryById(id);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ProductCategoryDto updateProductCategory(@PathVariable Long id, @RequestBody ProductCategoryDto category) {
    LOGGER.debug("Updating product category={} id={}", category, id);
    return service.updateCategory(category);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteProductCategory(@PathVariable Long id) {
    LOGGER.debug("Deleting category by id={}", id);
    service.deleteCategory(id);
  }

  @RequestMapping(value = "/{id}/products")
  public Collection<ProductDto> getProductsInCategory(@PathVariable Long id, @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "15") int size) {
    LOGGER.debug("Finding products for categoryId={} page={} size={}", id, page, size);
    return service.findAllProductsInCategory(id, page, size);
  }

  @Resource
  public void setService(ProductService service) {
    this.service = service;
  }

}
