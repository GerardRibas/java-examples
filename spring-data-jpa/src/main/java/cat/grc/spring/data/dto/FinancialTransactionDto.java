/**
 * 
 */
package cat.grc.spring.data.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class FinancialTransactionDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private Long accountId;

  private Long invoiceNumber;

  private Long typeCode;

  private Date transactionDate;

  private BigDecimal amount;

  public FinancialTransactionDto() {
    // Default Constructor
  }

  public FinancialTransactionDto(Long id, Long accountId, Long invoiceNumber, Long typeCode, Date transactionDate,
      BigDecimal amount) {
    super();
    this.id = id;
    this.accountId = accountId;
    this.invoiceNumber = invoiceNumber;
    this.typeCode = typeCode;
    this.transactionDate = transactionDate;
    this.amount = amount;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public Long getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceNumber(Long invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public Long getTypeCode() {
    return typeCode;
  }

  public void setTypeCode(Long typeCode) {
    this.typeCode = typeCode;
  }

  public Date getTransactionDate() {
    return transactionDate;
  }

  public void setTransactionDate(Date transactionDate) {
    this.transactionDate = transactionDate;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(id, accountId, invoiceNumber, typeCode, transactionDate, amount);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof FinancialTransactionDto) {
      FinancialTransactionDto that = (FinancialTransactionDto) object;
      return Objects.equal(this.id, that.id) && Objects.equal(this.accountId, that.accountId)
          && Objects.equal(this.invoiceNumber, that.invoiceNumber) && Objects.equal(this.typeCode, that.typeCode)
          && Objects.equal(this.transactionDate, that.transactionDate) && Objects.equal(this.amount, that.amount);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("accountId", accountId)
        .add("invoiceNumber", invoiceNumber).add("typeCode", typeCode).add("transactionDate", transactionDate)
        .add("amount", amount).toString();
  }



}
