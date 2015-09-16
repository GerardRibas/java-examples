/**
 * 
 */
package cat.grc.spring.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@Entity
@Table(name = "ORDER_ITEMS")
public class OrderItem implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private Order order;

  private Product product;

  private InvoiceLineItem invoiceLineItem;

  private Long quantity;

  private BigDecimal cost;

  public OrderItem() {
    // Default Constructor
  }

  public OrderItem(Long id, Order order, Product product, Long quantity, BigDecimal cost) {
    super();
    this.id = id;
    this.order = order;
    this.product = product;
    this.quantity = quantity;
    this.cost = cost;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ORDER_ITEM_ID")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne
  @JoinColumn(name = "ORDER_ID")
  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  @ManyToOne
  @JoinColumn(name = "PRODUCT_ID")
  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  @Column(name = "PRODUCT_QUANTITY")
  public Long getQuantity() {
    return quantity;
  }

  public void setQuantity(Long quantity) {
    this.quantity = quantity;
  }

  @Column(name = "ORDER_ITEM_COST")
  public BigDecimal getCost() {
    return cost;
  }

  public void setCost(BigDecimal cost) {
    this.cost = cost;
  }

  @OneToOne(mappedBy = "item")
  public InvoiceLineItem getInvoiceLineItem() {
    return invoiceLineItem;
  }

  public void setInvoiceLineItem(InvoiceLineItem invoiceLineItem) {
    this.invoiceLineItem = invoiceLineItem;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(id, order == null ? null : order.getId(), product == null ? null : product.getId(),
        invoiceLineItem == null ? null : invoiceLineItem.getId(), quantity, cost);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof OrderItem) {
      OrderItem that = (OrderItem) object;
      return Objects.equal(this.id, that.id)
          && Objects.equal(this.order == null ? null : this.order.getId(),
              that.order == null ? null : that.order.getId())
          && Objects.equal(this.product == null ? null : product.getId(),
              that.product == null ? null : that.product.getId())
          && Objects.equal(this.invoiceLineItem == null ? null : this.invoiceLineItem.getId(),
              that.invoiceLineItem == null ? null : that.invoiceLineItem.getId())
          && Objects.equal(this.quantity, that.quantity) && Objects.equal(this.cost, that.cost);
    }
    return false;
  }



  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("orderId", order == null ? null : order.getId())
        .add("product", product).add("quantity", quantity).add("cost", cost).toString();
  }

}
