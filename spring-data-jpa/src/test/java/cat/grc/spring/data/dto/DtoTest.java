/**
 * 
 */
package cat.grc.spring.data.dto;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.filters.FilterClassName;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.NoFieldShadowingRule;
import com.openpojo.validation.rule.impl.NoNestedClassRule;
import com.openpojo.validation.rule.impl.NoPrimitivesRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsRule;
import com.openpojo.validation.rule.impl.NoStaticExceptFinalRule;
import com.openpojo.validation.rule.impl.SerializableMustHaveSerialVersionUIDRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.DefaultValuesNullTester;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class DtoTest {

  private List<PojoClass> dtoClasses;

  private Validator validator;

  @Before
  public void before() {
    dtoClasses =
        PojoClassFactory.getPojoClasses(DtoTest.class.getPackage().getName(), new FilterClassName("^((?!Test$).)*$"));
    validator = ValidatorBuilder.create().with(new NoFieldShadowingRule()).with(new NoNestedClassRule())
        .with(new NoPrimitivesRule()).with(new NoPublicFieldsRule()).with(new NoStaticExceptFinalRule())
        .with(new SerializableMustHaveSerialVersionUIDRule()).with(new GetterMustExistRule())
        .with(new SetterMustExistRule()).with(new SetterTester()).with(new GetterTester())
        .with(new DefaultValuesNullTester()).build();
  }

  @Test
  public void testValidateEntities() {
    validator.validate(DtoTest.class.getPackage().getName(), new FilterClassName("^((?!Test$).)*$"));
  }

  @Test
  public void testEquals() {
    ProductDto product1 = new ProductDto();
    product1.setId(1L);
    ProductDto product2 = new ProductDto();
    product2.setId(2L);

    dtoClasses.forEach(entityClass -> EqualsVerifier.forClass(entityClass.getClazz()).suppress(Warning.NONFINAL_FIELDS)
        .withPrefabValues(ProductDto.class, product1, product2).verify());
  }

}
