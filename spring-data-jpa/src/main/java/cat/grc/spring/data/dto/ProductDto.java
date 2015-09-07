package cat.grc.spring.data.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class ProductDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private ProductDto parent;

  private ProductCategoryDto category;

  private String name;

  private BigDecimal price;

  private String color;

  private String size;

  private String description;

  public ProductDto() {
    // Default Constructor
  }

  public ProductDto(Long id) {
    super();
    this.id = id;
  }

  public ProductDto(Long id, ProductDto parent, ProductCategoryDto category, String name, BigDecimal price,
      String color, String size, String description) {
    super();
    this.id = id;
    this.parent = parent;
    this.category = category;
    this.name = name;
    this.price = price;
    this.color = color;
    this.size = size;
    this.description = description;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ProductDto getParent() {
    return parent;
  }

  public void setParent(ProductDto parent) {
    this.parent = parent;
  }

  public ProductCategoryDto getCategory() {
    return category;
  }

  public void setCategory(ProductCategoryDto category) {
    this.category = category;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(id, parent, category, name, price, color, size, description);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof ProductDto) {
      ProductDto that = (ProductDto) object;
      return Objects.equal(this.id, that.id) && Objects.equal(this.parent, that.parent)
          && Objects.equal(this.category, that.category) && Objects.equal(this.name, that.name)
          && Objects.equal(this.price, that.price) && Objects.equal(this.color, that.color)
          && Objects.equal(this.size, that.size) && Objects.equal(this.description, that.description);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("parent", parent).add("category", category)
        .add("name", name).add("price", price).add("color", color).add("size", size).add("description", description)
        .toString();
  }

}
