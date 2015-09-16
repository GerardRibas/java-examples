/**
 * 
 */
package cat.grc.spring.data.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class InvoiceLineItemDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private Long itemId;

  @JsonBackReference
  private InvoiceDto invoice;

  private Long productId;

  private String productTitle;

  private Long quantity;

  private BigDecimal price;

  private BigDecimal derivedProductCost;

  private BigDecimal derivedVatPayable;

  private BigDecimal derivedTotalCost;

  public InvoiceLineItemDto() {
    // Default constructor
  }

  public InvoiceLineItemDto(Long id, Long itemId, InvoiceDto invoice, Long productId, String productTitle,
      Long quantity, BigDecimal price, BigDecimal derivedProductCost, BigDecimal derivedVatPayable,
      BigDecimal derivedTotalCost) {
    super();
    this.id = id;
    this.itemId = itemId;
    this.invoice = invoice;
    this.productId = productId;
    this.productTitle = productTitle;
    this.quantity = quantity;
    this.price = price;
    this.derivedProductCost = derivedProductCost;
    this.derivedVatPayable = derivedVatPayable;
    this.derivedTotalCost = derivedTotalCost;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  public InvoiceDto getInvoice() {
    return invoice;
  }

  public void setInvoice(InvoiceDto invoice) {
    this.invoice = invoice;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public String getProductTitle() {
    return productTitle;
  }

  public void setProductTitle(String productTitle) {
    this.productTitle = productTitle;
  }

  public Long getQuantity() {
    return quantity;
  }

  public void setQuantity(Long quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public BigDecimal getDerivedProductCost() {
    return derivedProductCost;
  }

  public void setDerivedProductCost(BigDecimal derivedProductCost) {
    this.derivedProductCost = derivedProductCost;
  }

  public BigDecimal getDerivedVatPayable() {
    return derivedVatPayable;
  }

  public void setDerivedVatPayable(BigDecimal derivedVatPayable) {
    this.derivedVatPayable = derivedVatPayable;
  }

  public BigDecimal getDerivedTotalCost() {
    return derivedTotalCost;
  }

  public void setDerivedTotalCost(BigDecimal derivedTotalCost) {
    this.derivedTotalCost = derivedTotalCost;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(id, itemId, invoice, productId, productTitle, quantity, price, derivedProductCost,
        derivedVatPayable, derivedTotalCost);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof InvoiceLineItemDto) {
      InvoiceLineItemDto that = (InvoiceLineItemDto) object;
      return Objects.equal(this.id, that.id) && Objects.equal(this.itemId, that.itemId)
          && Objects.equal(this.invoice == null ? null : invoice.getNumber(),
              that.invoice == null ? null : that.invoice.getNumber())
          && Objects.equal(this.productId, that.productId) && Objects.equal(this.productTitle, that.productTitle)
          && Objects.equal(this.quantity, that.quantity) && Objects.equal(this.price, that.price)
          && Objects.equal(this.derivedProductCost, that.derivedProductCost)
          && Objects.equal(this.derivedVatPayable, that.derivedVatPayable)
          && Objects.equal(this.derivedTotalCost, that.derivedTotalCost);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("itemId", itemId)
        .add("invoiceNumber", invoice == null ? null : invoice.getNumber()).add("productId", productId)
        .add("productTitle", productTitle).add("quantity", quantity).add("price", price)
        .add("derivedProductCost", derivedProductCost).add("derivedVatPayable", derivedVatPayable)
        .add("derivedTotalCost", derivedTotalCost).toString();
  }

}
