package cat.grc.spring.data.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class InvoiceDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long number;

  private Long orderId;

  private Date created;

  @JsonManagedReference
  private Collection<InvoiceLineItemDto> lines;

  public InvoiceDto() {
    // Default Constructor
  }

  public InvoiceDto(Long orderId, Date created) {
    super();
    this.orderId = orderId;
    this.created = created;
  }

  public Long getNumber() {
    return number;
  }

  public void setNumber(Long number) {
    this.number = number;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Collection<InvoiceLineItemDto> getLines() {
    return lines;
  }

  public void setLines(Collection<InvoiceLineItemDto> lines) {
    this.lines = lines;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(number, orderId, created, lines);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof InvoiceDto) {
      InvoiceDto that = (InvoiceDto) object;
      return Objects.equal(this.number, that.number) && Objects.equal(this.orderId, that.orderId)
          && Objects.equal(this.created, that.created) && Objects.equal(this.lines, that.lines);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("number", number).add("orderId", orderId).add("created", created)
        .add("lines", lines).toString();
  }

}
