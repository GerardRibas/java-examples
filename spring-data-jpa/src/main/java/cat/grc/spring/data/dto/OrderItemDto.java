/**
 * 
 */
package cat.grc.spring.data.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class OrderItemDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private Long orderId;

  private ProductDto product;

  private Long quantity;

  private BigDecimal cost;

  public OrderItemDto() {

  }

  public OrderItemDto(Long id, Long orderId, ProductDto product, Long quantity, BigDecimal cost) {
    super();
    this.id = id;
    this.orderId = orderId;
    this.product = product;
    this.quantity = quantity;
    this.cost = cost;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public ProductDto getProduct() {
    return product;
  }

  public void setProduct(ProductDto product) {
    this.product = product;
  }

  public Long getQuantity() {
    return quantity;
  }

  public void setQuantity(Long quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getCost() {
    return cost;
  }

  public void setCost(BigDecimal cost) {
    this.cost = cost;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(id, orderId, product, quantity, cost);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof OrderItemDto) {
      OrderItemDto that = (OrderItemDto) object;
      return Objects.equal(this.id, that.id) && Objects.equal(this.orderId, that.orderId)
          && Objects.equal(this.product, that.product) && Objects.equal(this.quantity, that.quantity)
          && Objects.equal(this.cost, that.cost);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("orderId", orderId).add("product", product)
        .add("quantity", quantity).add("cost", cost).toString();
  }

}
