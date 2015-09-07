/**
 * 
 */
package cat.grc.spring.data.entity;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@Entity
@Table(name = "TRANSACTION_TYPES")
public class TransactionType implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long code;

  private String description;

  private Collection<FinancialTransaction> transactions;

  public TransactionType() {
    // Default Constructor
  }

  public TransactionType(Long code, String description) {
    super();
    this.code = code;
    this.description = description;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "TRANSACTION_TYPE_CODE")
  public Long getCode() {
    return code;
  }

  public void setCode(Long code) {
    this.code = code;
  }

  @Column(name = "TRANSACTION_TYPE_DESCRIPTION")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @OneToMany(mappedBy = "type")
  public Collection<FinancialTransaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(Collection<FinancialTransaction> transactions) {
    this.transactions = transactions;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(code, description, transactions);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof TransactionType) {
      TransactionType that = (TransactionType) object;
      return Objects.equal(this.code, that.code) && Objects.equal(this.description, that.description)
          && Objects.equal(this.transactions, that.transactions);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("code", code).add("description", description)
        .add("transactions", transactions).toString();
  }

}
