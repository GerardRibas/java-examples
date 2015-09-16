package cat.grc.spring.data.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.util.Assert;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@Entity
@Table(name = "INVOICES")
public class Invoice implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long number;

  private Order order;

  private Date created;

  private Collection<FinancialTransaction> transactions;

  private Collection<InvoiceLineItem> lines;

  public Invoice() {
    // Default Constructor
  }

  public Invoice(Order order, Date created) {
    Assert.notNull(order);
    Assert.notNull(order.getItems());
    this.order = order;
    this.created = created;
    this.lines = order.getItems().stream().map(item -> new InvoiceLineItem(item, this)).collect(Collectors.toList());
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "INVOICE_NUMBER")
  public Long getNumber() {
    return number;
  }

  public void setNumber(Long number) {
    this.number = number;
  }

  @ManyToOne
  @JoinColumn(name = "ORDER_ID")
  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  @Column(name = "INVOICE_DATE")
  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  @OneToMany(mappedBy = "invoice")
  public Collection<FinancialTransaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(Collection<FinancialTransaction> transactions) {
    this.transactions = transactions;
  }

  @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
  public Collection<InvoiceLineItem> getLines() {
    return lines;
  }

  public void setLines(Collection<InvoiceLineItem> lines) {
    this.lines = lines;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(number, order, created, transactions, lines);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof Invoice) {
      Invoice that = (Invoice) object;
      return Objects.equal(this.number, that.number) && Objects.equal(this.order, that.order)
          && Objects.equal(this.created, that.created) && Objects.equal(this.transactions, that.transactions)
          && Objects.equal(this.lines, that.lines);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("number", number).add("orderId", order == null ? null : order.getId())
        .add("created", created).add("transactions", transactions).add("lines", lines).toString();
  }



}
