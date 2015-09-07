/**
 * 
 */
package cat.grc.spring.data.dto;

import java.io.Serializable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class TransactionTypeDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long code;

  private String description;

  public TransactionTypeDto() {
    // Default Constructor
  }

  public TransactionTypeDto(Long code, String description) {
    super();
    this.code = code;
    this.description = description;
  }

  public Long getCode() {
    return code;
  }

  public void setCode(Long code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(code, description);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof TransactionTypeDto) {
      TransactionTypeDto that = (TransactionTypeDto) object;
      return Objects.equal(this.code, that.code) && Objects.equal(this.description, that.description);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("code", code).add("description", description).toString();
  }

}
