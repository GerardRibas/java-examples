/**
 * 
 */
package cat.grc.spring.mvc.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@SpringBootApplication
@EnableSwagger2
@ComponentScan("cat.grc.spring")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
