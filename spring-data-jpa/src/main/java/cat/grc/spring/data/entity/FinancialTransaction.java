/**
 * 
 */
package cat.grc.spring.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "FINANCIAL_TRANSACTIONS")
public class FinancialTransaction implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private Account account;

  private Invoice invoice;

  private TransactionType type;

  private Date transactionDate;

  private BigDecimal amount;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "TRANSACTION_ID")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne
  @JoinColumn(name = "ACCOUNT_ID")
  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
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
  @JoinColumn(name = "TRANSACTION_TYPE_CODE")
  public TransactionType getType() {
    return type;
  }

  public void setType(TransactionType type) {
    this.type = type;
  }

  @Column(name = "TRANSACTION_DATE")
  public Date getTransactionDate() {
    return transactionDate;
  }

  public void setTransactionDate(Date transactionDate) {
    this.transactionDate = transactionDate;
  }

  @Column(name = "TRANSACTION_AMOUNT")
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(id, account, invoice, type, transactionDate, amount);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof FinancialTransaction) {
      FinancialTransaction that = (FinancialTransaction) object;
      return Objects.equal(this.id, that.id) && Objects.equal(this.account, that.account)
          && Objects.equal(this.invoice, that.invoice) && Objects.equal(this.type, that.type)
          && Objects.equal(this.transactionDate, that.transactionDate) && Objects.equal(this.amount, that.amount);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("account", account).add("invoice", invoice)
        .add("type", type).add("transactionDate", transactionDate).add("amount", amount).toString();
  }



}
