package cat.grc.spring.data.entity;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@Entity
@Table(name = "PRODUCT_CATEGORIES")
public class ProductCategory implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long code;

  private String description;

  private Float vatRating;

  private Collection<Product> products;

  public ProductCategory() {

  }

  public ProductCategory(Long code, String description, Float vatRating) {
    super();
    this.code = code;
    this.description = description;
    this.vatRating = vatRating;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "PRODUCT_TYPE_CODE")
  public Long getCode() {
    return code;
  }

  public void setCode(Long code) {
    this.code = code;
  }

  @Column(name = "PRODUCT_TYPE_DESCRIPTION")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "VAT_RATING")
  public Float getVatRating() {
    return vatRating;
  }

  public void setVatRating(Float vatRating) {
    this.vatRating = vatRating;
  }

  @OneToMany(mappedBy = "category")
  public Collection<Product> getProducts() {
    return products;
  }

  public void setProducts(Collection<Product> products) {
    this.products = products;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(code, description, vatRating, products);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof ProductCategory) {
      ProductCategory that = (ProductCategory) object;
      return Objects.equal(this.code, that.code) && Objects.equal(this.description, that.description)
          && Objects.equal(this.vatRating, that.vatRating) && Objects.equal(this.products, that.products);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("code", code).add("description", description)
        .add("vatRating", vatRating).add("products", products).toString();
  }

}
