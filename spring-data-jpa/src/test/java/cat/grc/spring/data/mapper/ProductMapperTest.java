/**
 * 
 */
package cat.grc.spring.data.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.Test;

import cat.grc.spring.data.dto.ProductCategoryDto;
import cat.grc.spring.data.dto.ProductDto;
import cat.grc.spring.data.entity.Product;
import cat.grc.spring.data.entity.ProductCategory;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class ProductMapperTest {

  @Test
  public void testPrivateConstructorIsPrivate() {
    final Constructor<?>[] constructors = ProductMapper.class.getDeclaredConstructors();
    for (Constructor<?> constructor : constructors) {
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }
  }

  @Test
  public void testPricateConstructor() throws Exception {
    final Constructor<?>[] constructors = ProductMapper.class.getDeclaredConstructors();
    constructors[0].setAccessible(true);
    constructors[0].newInstance((Object[]) null);
  }

  @Test
  public void testToDto() {
    ProductCategory category = new ProductCategory(1L, "Animals & Pet Supplies", new Float("0.2"));
    Product parentProduct =
        new Product(1L, null, category, "Product Name", new BigDecimal("10.22"), "Blue", "10x10", "Some Description");
    Product entity = new Product(2L, parentProduct, category, "Some another name", new BigDecimal("20.22"), "Red",
        "20x20", "Some another description");

    ProductDto dto = ProductMapper.toDto(entity);
    assertNotNull("Expected an object", dto);
    assertEquals("Expected the same id", entity.getId(), dto.getId());
    assertEquals("Expected the same color", entity.getColor(), dto.getColor());
    assertEquals("Expected the same description", entity.getDescription(), dto.getDescription());
    assertEquals("Expected the same name", entity.getName(), dto.getName());
    assertEquals("Expected the same price", entity.getPrice(), dto.getPrice());
    assertEquals("Expected the same size", entity.getSize(), dto.getSize());
    assertNotNull("Expected a Category", dto.getCategory());

    assertEquals("Expected the same Parent id", entity.getParent().getId(), dto.getParent().getId());
    assertEquals("Expected the same Parent color", entity.getParent().getColor(), dto.getParent().getColor());
    assertEquals("Expected the same Parent description", entity.getParent().getDescription(),
        dto.getParent().getDescription());
    assertEquals("Expected the same Parent name", entity.getParent().getName(), dto.getParent().getName());
    assertEquals("Expected the same Parent price", entity.getParent().getPrice(), dto.getParent().getPrice());
    assertEquals("Expected the same Parent size", entity.getParent().getSize(), dto.getParent().getSize());
    assertNotNull("Expected a Parent Category", dto.getParent().getCategory());
  }

  @Test
  public void testToEntity() {
    ProductCategoryDto category = new ProductCategoryDto(1L, "Animals & Pet Supplies", new Float("0.2"));
    ProductDto parentProduct = new ProductDto(1L, null, category, "Product Name", new BigDecimal("10.22"), "Blue",
        "10x10", "Some Description");
    ProductDto dto = new ProductDto(2L, parentProduct, category, "Some another name", new BigDecimal("20.22"), "Red",
        "20x20", "Some another description");

    Product entity = ProductMapper.toEntity(dto);
    assertNotNull("Expected an object", entity);
    assertEquals("Expected the same id", dto.getId(), entity.getId());
    assertEquals("Expected the same color", dto.getColor(), entity.getColor());
    assertEquals("Expected the same description", dto.getDescription(), entity.getDescription());
    assertEquals("Expected the same name", dto.getName(), entity.getName());
    assertEquals("Expected the same price", dto.getPrice(), entity.getPrice());
    assertEquals("Expected the same size", dto.getSize(), entity.getSize());
    assertNotNull("Expected a Category", entity.getCategory());

    assertEquals("Expected the same Parent id", dto.getParent().getId(), entity.getParent().getId());
    assertEquals("Expected the same Parent color", dto.getParent().getColor(), entity.getParent().getColor());
    assertEquals("Expected the same Parent description", dto.getParent().getDescription(),
        entity.getParent().getDescription());
    assertEquals("Expected the same Parent name", dto.getParent().getName(), entity.getParent().getName());
    assertEquals("Expected the same Parent price", dto.getParent().getPrice(), entity.getParent().getPrice());
    assertEquals("Expected the same Parent size", dto.getParent().getSize(), entity.getParent().getSize());
    assertNotNull("Expected a Parent Category", entity.getParent().getCategory());
  }

}

