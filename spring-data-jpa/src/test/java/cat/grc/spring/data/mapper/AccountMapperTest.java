/**
 * 
 */
package cat.grc.spring.data.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Date;

import org.junit.Test;

import cat.grc.spring.data.dto.AccountDto;
import cat.grc.spring.data.entity.Account;
import cat.grc.spring.data.entity.Customer;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class AccountMapperTest {

  @Test
  public void testPrivateConstructorIsPrivate() {
    final Constructor<?>[] constructors = AccountMapper.class.getDeclaredConstructors();
    for (Constructor<?> constructor : constructors) {
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }
  }

  @Test
  public void testPricateConstructor() throws Exception {
    final Constructor<?>[] constructors = AccountMapper.class.getDeclaredConstructors();
    constructors[0].setAccessible(true);
    constructors[0].newInstance((Object[]) null);
  }

  @Test
  public void testToDto() {
    Account account = new Account(1L, new Customer(1L), new Date(), "Some account");
    AccountDto expectedDto =
        new AccountDto(account.getId(), account.getCustomer().getId(), account.getOpened(), account.getName());
    AccountDto dto = AccountMapper.toDto(account);
    assertEquals("Dtos are not equals as expected", expectedDto, dto);
  }

  @Test
  public void testToEntity() {
    AccountDto account = new AccountDto(1L, 1L, new Date(), "Some account");
    Account expectedEntity =
        new Account(account.getId(), new Customer(account.getCustomer()), account.getOpened(), account.getName());
    Account entity = AccountMapper.toEntity(account);
    assertEquals("Entities are not equals as expected", expectedEntity, entity);
  }

  @Test
  public void testToDto_NullEntity() {
    Account entity = null;
    AccountDto dto = AccountMapper.toDto(entity);
    assertNull("Expected a null value", dto);
  }

  @Test
  public void testToEntity_NullDto() {
    AccountDto dto = null;
    Account entity = AccountMapper.toEntity(dto);
    assertNull("Expected a null value", entity);
  }

}
