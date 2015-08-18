/**
 * 
 */
package cat.grc.spring.data.entity;

import java.io.Serializable;
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
@Table(name = "ACCOUNTS")
public class Account implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private Customer customer;

  private Date opened;

  private String name;

  private Collection<FinancialTransaction> transactions;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ACCOUNT_ID")
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

  @Column(name = "DATE_ACCOUNT_OPENED")
  public Date getOpened() {
    return opened;
  }

  public void setOpened(Date opened) {
    this.opened = opened;
  }

  @Column(name = "ACCOUNT_NAME")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @OneToMany(mappedBy = "account")
  public Collection<FinancialTransaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(Collection<FinancialTransaction> transactions) {
    this.transactions = transactions;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(id, customer, opened, name, transactions);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof Account) {
      Account that = (Account) object;
      return Objects.equal(this.id, that.id) && Objects.equal(this.customer, that.customer)
          && Objects.equal(this.opened, that.opened) && Objects.equal(this.name, that.name)
          && Objects.equal(this.transactions, that.transactions);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("customer", customer).add("opened", opened)
        .add("name", name).add("transactions", transactions).toString();
  }



}
