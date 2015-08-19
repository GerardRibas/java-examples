/**
 * 
 */
package cat.grc.spring.data.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Test;

import cat.grc.spring.data.Gender;
import cat.grc.spring.data.dto.CustomerDto;
import cat.grc.spring.data.entity.Customer;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class CustomerMapperTest {

  @Test
  public void testPrivateConstructorIsPrivate() {
    final Constructor<?>[] constructors = CustomerMapper.class.getDeclaredConstructors();
    for (Constructor<?> constructor : constructors) {
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }
  }

  @Test
  public void testPricateConstructor() throws Exception {
    final Constructor<?>[] constructors = CustomerMapper.class.getDeclaredConstructors();
    constructors[0].setAccessible(true);
    constructors[0].newInstance((Object[]) null);
  }

  @Test
  public void testToDto() {
    Customer entity = new Customer(1L, "Bart", "JoJo", "Simpson", Gender.MALE, "bart@simpson.com", "55512345",
        "742 Evergreen Terrace, Sprinfield");
    CustomerDto expectedDto = new CustomerDto(entity.getId(), entity.getFirstName(), entity.getMiddleName(),
        entity.getLastName(), entity.getGender(), entity.getEmail(), entity.getPhoneNumber(), entity.getAddress());
    CustomerDto dto = CustomerMapper.toDto(entity);
    assertEquals("Dto are not equals as expected", expectedDto, dto);
  }

  @Test
  public void testToEntity() {
    CustomerDto dto = new CustomerDto(1L, "Bart", "JoJo", "Simpson", Gender.MALE, "bart@simpson.com", "55512345",
        "742 Evergreen Terrace, Sprinfield");
    Customer expectedEntity = new Customer(dto.getId(), dto.getFirstName(), dto.getMiddleName(), dto.getLastName(),
        dto.getGender(), dto.getEmail(), dto.getPhoneNumber(), dto.getAddress());
    Customer entity = CustomerMapper.toEntity(dto);
    assertEquals("Entities are not equals as expected", expectedEntity, entity);
  }

  @Test
  public void testToDto_NullEntity() {
    Customer entity = null;
    CustomerDto dto = CustomerMapper.toDto(entity);
    assertNull("Expected a null value", dto);
  }

  @Test
  public void testToEntity_NullDto() {
    CustomerDto dto = null;
    Customer entity = CustomerMapper.toEntity(dto);
    assertNull("Expected a null value", entity);
  }

}
