/**
 * 
 */
package cat.grc.spring.data.mapper;

import cat.grc.spring.data.dto.ProductCategoryDto;
import cat.grc.spring.data.entity.ProductCategory;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class ProductCategoryMapper {

  private ProductCategoryMapper() {

  }

  public static final ProductCategoryDto toDto(ProductCategory entity) {
    ProductCategoryDto result;
    if (entity == null) {
      result = null;
    } else {
      result = new ProductCategoryDto(entity.getCode(), entity.getDescription(), entity.getVatRating());
    }
    return result;
  }

  public static final ProductCategory toEntity(ProductCategoryDto dto) {
    ProductCategory result;
    if (dto == null) {
      result = null;
    } else {
      result = new ProductCategory(dto.getCode(), dto.getDescription(), dto.getVatRating());
    }
    return result;
  }

}
