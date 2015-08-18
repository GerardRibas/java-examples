package cat.grc.spring.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@Entity
@Table(name = "ORDERS")
public class Order implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private Customer customer;

  private Date placed;

  private BigDecimal total;

  private Collection<Invoice> invoices;

  private Collection<OrderItem> items;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ORDER_ID")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne
  @JoinColumn(name = "CUSTOMER_ID")
  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  @Column(name = "DATE_ORDER_PLACED")
  public Date getPlaced() {
    return placed;
  }

  public void setPlaced(Date placed) {
    this.placed = placed;
  }

  @Column(name = "TOTAL_AMOUNT")
  public BigDecimal getTotal() {
    return total;
  }

  public void setTotal(BigDecimal total) {
    this.total = total;
  }

  @OneToMany(mappedBy = "order")
  public Collection<Invoice> getInvoices() {
    return invoices;
  }

  public void setInvoices(Collection<Invoice> invoices) {
    this.invoices = invoices;
  }

  @OneToMany(mappedBy = "order")
  public Collection<OrderItem> getItems() {
    return items;
  }

  public void setItems(Collection<OrderItem> items) {
    this.items = items;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(id, customer, placed, total, invoices, items);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof Order) {
      Order that = (Order) object;
      return Objects.equal(this.id, that.id) && Objects.equal(this.customer, that.customer)
          && Objects.equal(this.placed, that.placed) && Objects.equal(this.total, that.total)
          && Objects.equal(this.invoices, that.invoices) && Objects.equal(this.items, that.items);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("customer", customer).add("placed", placed)
        .add("total", total).add("invoices", invoices).add("items", items).toString();
  }



}
