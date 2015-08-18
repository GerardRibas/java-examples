package cat.grc.spring.data.mapper;

import cat.grc.spring.data.dto.ProductDto;
import cat.grc.spring.data.entity.Product;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class ProductMapper {

  private ProductMapper() {

  }

  public static final ProductDto toDto(Product entity) {
    ProductDto result;
    if (entity == null) {
      result = null;
    } else {
      result = new ProductDto(entity.getId(), ProductMapper.toDto(entity.getParent()),
          ProductCategoryMapper.toDto(entity.getCategory()), entity.getName(), entity.getPrice(), entity.getColor(),
          entity.getSize(), entity.getDescription());
    }
    return result;
  }

  public static final Product toEntity(ProductDto dto) {
    Product result;
    if (dto == null) {
      result = null;
    } else {
      result = new Product(dto.getId(), ProductMapper.toEntity(dto.getParent()),
          ProductCategoryMapper.toEntity(dto.getCategory()), dto.getName(), dto.getPrice(), dto.getColor(),
          dto.getSize(), dto.getDescription());
    }
    return result;
  }

}
