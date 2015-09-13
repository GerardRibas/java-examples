/**
 * 
 */
package cat.grc.spring.data;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import cat.grc.spring.data.dto.InvoiceDto;
import cat.grc.spring.data.dto.OrderDto;
import cat.grc.spring.data.entity.Invoice;
import cat.grc.spring.data.entity.Order;
import liquibase.integration.spring.SpringLiquibase;

/**
 * @author Gerard Ribas (gerard.ribas.canals@gmail.com)
 *
 */
@Configuration
@EnableTransactionManagement
@ComponentScan("cat.grc.spring.data")
@EnableJpaRepositories(basePackages = "cat.grc.spring.data.repository")
@PropertySource("classpath:application.properties")
public class EntityManagerConfiguration {

  private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
  private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
  private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
  private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";
  private static final String PROPERTY_NAME_DATABASE_CACHE_PREP_STMTS = "db.cachePrepStmts";
  private static final String PROPERTY_NAME_DATABASE_PREP_STMT_CACHE_SIZE = "db.prepStmtCacheSize";
  private static final String PROPERTY_NAME_DATABASE_PREP_STMT_CACHE_SQL_LIMIT = "db.prepStmtCacheSqlLimit";

  private static final String PROPERTY_NAME_LIQUIBASE_CONTEXTS = "liquibase.contexts";

  private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
  private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
  private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
  private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";

  @Autowired
  private Environment environment;

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory);
    return transactionManager;
  }

  @Bean
  public JpaVendorAdapter jpaVendorAdapter() {
    return new HibernateJpaVendorAdapter();
  }

  @Bean
  public DataSource dataSource() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(environment.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
    config.setUsername(environment.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
    config.setPassword(environment.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));
    config.setDriverClassName(environment.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
    config.addDataSourceProperty("cachePrepStmts",
        environment.getRequiredProperty(PROPERTY_NAME_DATABASE_CACHE_PREP_STMTS));
    config.addDataSourceProperty("prepStmtCacheSize",
        environment.getRequiredProperty(PROPERTY_NAME_DATABASE_PREP_STMT_CACHE_SIZE));
    config.addDataSourceProperty("prepStmtCacheSqlLimit",
        environment.getRequiredProperty(PROPERTY_NAME_DATABASE_PREP_STMT_CACHE_SQL_LIMIT));
    return new HikariDataSource(config);
  }

  @Bean
  public SpringLiquibase liquibase(DataSource dataSource) {
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setDataSource(dataSource);
    liquibase.setContexts(environment.getRequiredProperty(PROPERTY_NAME_LIQUIBASE_CONTEXTS));
    liquibase.setChangeLog("classpath:liquibase/db-changelog-master.xml");
    return liquibase;
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
      JpaVendorAdapter jpaVendorAdapter) {
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setDataSource(dataSource);
    factory.setPackagesToScan("cat.grc.spring.data.entity");
    factory.setJpaVendorAdapter(jpaVendorAdapter);

    Properties jpaProperties = new Properties();
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_DIALECT,
        environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL,
        environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY,
        environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY));
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL,
        environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));

    factory.setJpaProperties(jpaProperties);
    return factory;
  }

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper.addMappings(new PropertyMap<Order, OrderDto>() {
      @Override
      protected void configure() {
        skip().setItems(null);
      }
    });
    mapper.addMappings(new PropertyMap<Invoice, InvoiceDto>() {
      @Override
      protected void configure() {
        skip().setLines(null);
      }
    });

    return mapper;
  }

}
