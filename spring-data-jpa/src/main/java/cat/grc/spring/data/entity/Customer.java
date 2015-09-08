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

import cat.grc.spring.data.Gender;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@Entity
@Table(name = "CUSTOMERS")
public class Customer implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String firstName;

  private String middleName;

  private String lastName;

  private Gender gender;

  private String email;

  private String phoneNumber;

  private String address;

  private Collection<Account> accounts;

  private Collection<Order> orders;

  public Customer() {
    // Default Constructor
  }

  public Customer(Long id) {
    super();
    this.id = id;
  }

  public Customer(Long id, String firstName, String middleName, String lastName, Gender gender, String email,
      String phoneNumber, String address) {
    super();
    this.id = id;
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
    this.gender = gender;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.address = address;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "CUSTOMER_ID")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "CUSTOMER_FIRST_NAME")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @Column(name = "CUSTOMER_MIDDLE_NAME")
  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  @Column(name = "CUSTOMER_LAST_NAME")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Column(name = "GENDER")
  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  @Column(name = "EMAIL_ADDRESS")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Column(name = "PHONE_NUMBER")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @Column(name = "ADDRESS_DETAILS")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @OneToMany(mappedBy = "customer")
  public Collection<Account> getAccounts() {
    return accounts;
  }

  public void setAccounts(Collection<Account> accounts) {
    this.accounts = accounts;
  }

  @OneToMany(mappedBy = "customer")
  public Collection<Order> getOrders() {
    return orders;
  }

  public void setOrders(Collection<Order> orders) {
    this.orders = orders;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(id, firstName, middleName, lastName, gender, email, phoneNumber, address, accounts, orders);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof Customer) {
      Customer that = (Customer) object;
      return Objects.equal(this.id, that.id) && Objects.equal(this.firstName, that.firstName)
          && Objects.equal(this.middleName, that.middleName) && Objects.equal(this.lastName, that.lastName)
          && Objects.equal(this.gender, that.gender) && Objects.equal(this.email, that.email)
          && Objects.equal(this.phoneNumber, that.phoneNumber) && Objects.equal(this.address, that.address)
          && Objects.equal(this.accounts, that.accounts) && Objects.equal(this.orders, that.orders);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("firstName", firstName).add("middleName", middleName)
        .add("lastName", lastName).add("gender", gender).add("email", email).add("phoneNumber", phoneNumber)
        .add("address", address).add("accounts", accounts).add("orders", orders).toString();
  }

}
