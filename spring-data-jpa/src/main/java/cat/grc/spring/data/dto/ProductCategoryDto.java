package cat.grc.spring.data.dto;

import java.io.Serializable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class ProductCategoryDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long code;

  private String description;

  private Float vatRating;

  public ProductCategoryDto() {

  }

  public ProductCategoryDto(Long code, String description, Float vatRating) {
    super();
    this.code = code;
    this.description = description;
    this.vatRating = vatRating;
  }

  public Long getCode() {
    return code;
  }

  public void setCode(Long code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Float getVatRating() {
    return vatRating;
  }

  public void setVatRating(Float vatRating) {
    this.vatRating = vatRating;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(code, description, vatRating);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof ProductCategoryDto) {
      ProductCategoryDto that = (ProductCategoryDto) object;
      return Objects.equal(this.code, that.code) && Objects.equal(this.description, that.description)
          && Objects.equal(this.vatRating, that.vatRating);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("code", code).add("description", description)
        .add("vatRating", vatRating).toString();
  }

}
