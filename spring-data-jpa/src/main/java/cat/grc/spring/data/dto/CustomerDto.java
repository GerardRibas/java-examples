package cat.grc.spring.data.dto;

import java.io.Serializable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import cat.grc.spring.data.Gender;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class CustomerDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String firstName;

  private String middleName;

  private String lastName;

  private Gender gender;

  private String email;

  private String phoneNumber;

  private String address;

  public CustomerDto() {
    // Default Constructor
  }

  public CustomerDto(Long id, String firstName, String middleName, String lastName, Gender gender, String email,
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



  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(id, firstName, middleName, lastName, gender, email, phoneNumber, address);
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof CustomerDto) {
      CustomerDto that = (CustomerDto) object;
      return Objects.equal(this.id, that.id) && Objects.equal(this.firstName, that.firstName)
          && Objects.equal(this.middleName, that.middleName) && Objects.equal(this.lastName, that.lastName)
          && Objects.equal(this.gender, that.gender) && Objects.equal(this.email, that.email)
          && Objects.equal(this.phoneNumber, that.phoneNumber) && Objects.equal(this.address, that.address);
    }
    return false;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("firstName", firstName).add("middleName", middleName)
        .add("lastName", lastName).add("gender", gender).add("email", email).add("phoneNumber", phoneNumber)
        .add("address", address).toString();
  }

}
