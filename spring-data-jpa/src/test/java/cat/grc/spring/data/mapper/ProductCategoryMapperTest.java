package cat.grc.spring.data.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Test;

import cat.grc.spring.data.dto.ProductCategoryDto;
import cat.grc.spring.data.entity.ProductCategory;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class ProductCategoryMapperTest {

  @Test
  public void testPrivateConstructorIsPrivate() {
    final Constructor<?>[] constructors = ProductCategoryMapper.class.getDeclaredConstructors();
    for (Constructor<?> constructor : constructors) {
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }
  }

  @Test
  public void testPricateConstructor() throws Exception {
    final Constructor<?>[] constructors = ProductCategoryMapper.class.getDeclaredConstructors();
    constructors[0].setAccessible(true);
    constructors[0].newInstance((Object[]) null);
  }

  @Test
  public void testToDto() {
    ProductCategory entity = new ProductCategory(1L, "Animals & Pet Supplies", new Float("0.2"));
    ProductCategoryDto dto = ProductCategoryMapper.toDto(entity);
    assertEquals("Expected the same code", entity.getCode(), dto.getCode());
    assertEquals("Expected the same description", entity.getDescription(), dto.getDescription());
    assertEquals("Expected the same vat rating", entity.getVatRating(), dto.getVatRating());
  }

  @Test
  public void testToEntity() {
    ProductCategoryDto dto = new ProductCategoryDto(1L, "Animals & Pet Supplies", new Float("0.2"));
    ProductCategory entity = ProductCategoryMapper.toEntity(dto);
    assertEquals("Expected the same code", dto.getCode(), entity.getCode());
    assertEquals("Expected the same description", dto.getDescription(), entity.getDescription());
    assertEquals("Expected the same vat rating", dto.getVatRating(), entity.getVatRating());
  }

  @Test
  public void testToDto_NullEntity() {
    ProductCategory entity = null;
    ProductCategoryDto dto = ProductCategoryMapper.toDto(entity);
    assertNull("Expected a null value", dto);
  }

  @Test
  public void testToEntity_NullDto() {
    ProductCategoryDto dto = null;
    ProductCategory entity = ProductCategoryMapper.toEntity(dto);
    assertNull("Expected a null value", entity);
  }


}
