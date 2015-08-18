/**
 * 
 */
package cat.grc.spring.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@Entity
@Table(name = "INVOICE_LINE_ITEMS")
public class InvoiceLineItem implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private OrderItem item;

  private Invoice invoice;

  private Product product;

  private String productTitle;

  private Long quantity;

  private BigDecimal price;

  private BigDecimal derivedProductCost;

  private BigDecimal derivedVatPayable;

  private BigDecimal derivedTotalCost;

  @Id
  @Column(name = "ORDER_ITEM_ID")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne
  @JoinColumn(name = "ORDER_ITEM_ID", insertable = false, updatable = false)
  public OrderItem getItem() {
    return item;
  }

  public void setItem(OrderItem item) {
    this.item = item;
  }

  @ManyToOne
  @JoinColumn(name = "INVOICE_NUMBER")
  public Invoice getInvoice() {
    return invoice;
  }

  public void setInvoice(Invoice invoice) {
    this.invoice = invoice;
  }

  @ManyToOne
  @JoinColumn(name = "PRODUCT_ID")
  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  @Column(name = "PRODUCT_TITLE")
  public String getProductTitle() {
    return productTitle;
  }

  public void setProductTitle(String productTitle) {
    this.productTitle = productTitle;
  }

  @Column(name = "PRODUCT_QUANTITY")
  public Long getQuantity() {
    return quantity;
  }

  public void setQuantity(Long quantity) {
    this.quantity = quantity;
  }

  @Column(name = "PRODUCT_PRICE")
  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  @Column(name = "DERIVED_PRODUCT_COST")
  public BigDecimal getDerivedProductCost() {
    return derivedProductCost;
  }

  public void setDerivedProductCost(BigDecimal derivedProductCost) {
    this.derivedProductCost = derivedProductCost;
  }

  @Column(name = "DERIVED_VAT_PAYABLE")
  public BigDecimal getDerivedVatPayable() {
    return derivedVatPayable;
  }

  public void setDerivedVatPayable(BigDecimal derivedVatPayable) {
    this.derivedVatPayable = derivedVatPayable;
  }

  @Column(name = "DERIVED_TOTAL_COST")
  public BigDecimal getDerivedTotalCost() {
    return derivedTotalCost;
  }

  public void setDerivedTotalCost(BigDecimal derivedTotalCost) {
    this.derivedTotalCost = derivedTotalCost;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(id, item, invoice, product, productTitle, quantity, price, derivedProductCost,
        derivedVatPayable, derivedTotalCost);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof InvoiceLineItem) {
      InvoiceLineItem that = (InvoiceLineItem) object;
      return Objects.equal(this.id, that.id) && Objects.equal(this.item, that.item)
          && Objects.equal(this.invoice, that.invoice) && Objects.equal(this.product, that.product)
          && Objects.equal(this.productTitle, that.productTitle) && Objects.equal(this.quantity, that.quantity)
          && Objects.equal(this.price, that.price) && Objects.equal(this.derivedProductCost, that.derivedProductCost)
          && Objects.equal(this.derivedVatPayable, that.derivedVatPayable)
          && Objects.equal(this.derivedTotalCost, that.derivedTotalCost);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("item", item).add("invoice", invoice)
        .add("product", product).add("productTitle", productTitle).add("quantity", quantity).add("price", price)
        .add("derivedProductCost", derivedProductCost).add("derivedVatPayable", derivedVatPayable)
        .add("derivedTotalCost", derivedTotalCost).toString();
  }



}
