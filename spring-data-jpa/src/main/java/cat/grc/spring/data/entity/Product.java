package cat.grc.spring.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

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
@Table(name = "PRODUCTS")
public class Product implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private Product parent;

  private ProductCategory category;

  private String name;

  private BigDecimal price;

  private String color;

  private String size;

  private String description;

  private Collection<OrderItem> orderItems;

  private Collection<InvoiceLineItem> invoiceItems;

  public Product() {

  }

  public Product(Long id) {
    super();
    this.id = id;
  }

  public Product(Long id, Product parent, ProductCategory category, String name, BigDecimal price, String color,
      String size, String description) {
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

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "PRODUCT_ID")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne
  @JoinColumn(name = "PARENT_PRODUCT_ID")
  public Product getParent() {
    return parent;
  }

  public void setParent(Product parent) {
    this.parent = parent;
  }

  @ManyToOne
  @JoinColumn(name = "PRODUCT_TYPE_CODE")
  public ProductCategory getCategory() {
    return category;
  }

  public void setCategory(ProductCategory category) {
    this.category = category;
  }

  @Column(name = "PRODUCT_NAME")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "PRODUCT_PRICE")
  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  @Column(name = "PRODUCT_COLOR")
  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  @Column(name = "PRODUCT_SIZE")
  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  @Column(name = "PRODUCT_DESCRIPTION")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @OneToMany(mappedBy = "product")
  public Collection<InvoiceLineItem> getInvoiceItems() {
    return invoiceItems;
  }

  public void setInvoiceItems(Collection<InvoiceLineItem> invoiceItems) {
    this.invoiceItems = invoiceItems;
  }

  @OneToMany(mappedBy = "product")
  public Collection<OrderItem> getOrderItems() {
    return orderItems;
  }

  public void setOrderItems(Collection<OrderItem> orderItems) {
    this.orderItems = orderItems;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(id, parent, category, name, price, color, size, description, orderItems, invoiceItems);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof Product) {
      Product that = (Product) object;
      return Objects.equal(this.id, that.id) && Objects.equal(this.parent, that.parent)
          && Objects.equal(this.category, that.category) && Objects.equal(this.name, that.name)
          && Objects.equal(this.price, that.price) && Objects.equal(this.color, that.color)
          && Objects.equal(this.size, that.size) && Objects.equal(this.description, that.description)
          && Objects.equal(this.orderItems, that.orderItems) && Objects.equal(this.invoiceItems, that.invoiceItems);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("parent", parent).add("category", category)
        .add("name", name).add("price", price).add("color", color).add("size", size).add("description", description)
        .add("orderItems", orderItems).add("invoiceItems", invoiceItems).toString();
  }



}
