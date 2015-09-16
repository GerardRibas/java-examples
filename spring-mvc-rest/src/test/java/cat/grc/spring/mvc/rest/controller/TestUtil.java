/**
 * 
 */
package cat.grc.spring.mvc.rest.controller;

import java.lang.reflect.Method;
import java.nio.charset.Charset;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
public class TestUtil {

  protected static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));


  protected static ExceptionHandlerExceptionResolver createExceptionResolver() {
    ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
      @Override
      protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod,
          Exception exception) {
        Method method = new ExceptionHandlerMethodResolver(ExceptionHandlerController.class).resolveMethod(exception);
        return new ServletInvocableHandlerMethod(new ExceptionHandlerController(), method);
      }
    };
    exceptionResolver.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    exceptionResolver.afterPropertiesSet();
    return exceptionResolver;
  }

}
