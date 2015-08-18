package cat.grc.spring.data.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cat.grc.spring.data.EntityManagerConfiguration;
import cat.grc.spring.data.entity.Product;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EntityManagerConfiguration.class)
public class ProductRepositoryTest {

  @Autowired
  private ProductRepository repository;

  @Test
  public void testFindByCategory() {
    PageRequest pageRequest = new PageRequest(0, 10);
    Long categoryId = 6L;
    Page<Product> page = repository.findByCategory(categoryId, pageRequest);
    assertEquals(6, page.getNumberOfElements());
  }

}
