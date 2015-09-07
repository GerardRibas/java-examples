package cat.grc.spring.data.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class OrderDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private Long customerId;

  private Date placed;

  private BigDecimal total;

  private Collection<OrderItemDto> items;

  public OrderDto() {
    // Default Constructor
  }

  public OrderDto(Long id, Long customerId, Date placed, BigDecimal total) {
    super();
    this.id = id;
    this.customerId = customerId;
    this.placed = placed;
    this.total = total;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Date getPlaced() {
    return placed;
  }

  public void setPlaced(Date placed) {
    this.placed = placed;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public void setTotal(BigDecimal total) {
    this.total = total;
  }

  public Collection<OrderItemDto> getItems() {
    return items;
  }

  public void setItems(Collection<OrderItemDto> items) {
    this.items = items;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(id, customerId, placed, total, items);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof OrderDto) {
      OrderDto that = (OrderDto) object;
      return Objects.equal(this.id, that.id) && Objects.equal(this.customerId, that.customerId)
          && Objects.equal(this.placed, that.placed) && Objects.equal(this.total, that.total)
          && Objects.equal(this.items, that.items);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("customerId", customerId).add("placed", placed)
        .add("total", total).toString();
  }

}
