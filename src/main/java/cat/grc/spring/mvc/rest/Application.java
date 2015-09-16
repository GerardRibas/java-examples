/**
 * 
 */
package cat.grc.spring.mvc.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@SpringBootApplication
@ComponentScan("cat.grc.spring")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
