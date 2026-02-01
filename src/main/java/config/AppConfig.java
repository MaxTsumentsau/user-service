package config;

import annotation.ServiceFactory;
import dao.UserDao;
import dao.UserDaoImpl;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import service.UserService;
import service.UserServiceImpl;
import ui.ConsoleMenu;
import ui.ConsoleRenderer;
import ui.ErrorHandler;
import ui.InputValidator;

import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
public class AppConfig {

     private final SessionFactory sessionFactory;
     private final ServiceFactory serviceFactory;

     public AppConfig() {
          Dotenv env = Dotenv.load();

          DataSource dataSource = DataSourceFactory.create(env);

          Flyway flyway = Flyway.configure()
                  .dataSource(dataSource)
                  .locations("classpath:db/migration")
                  .load();
          flyway.migrate();

          Properties props = loadHibernateProps();
          this.sessionFactory = HibernateUtil.buildSessionFactory(props, dataSource);

          this.serviceFactory = new ServiceFactory(sessionFactory);
     }

     private Properties loadHibernateProps() {
          log.info("Loading Hibernate properties from hibernate.properties");

          Properties props = new Properties();
          try (var stream = getClass().getClassLoader().getResourceAsStream("hibernate.properties")) {
               props.load(stream);
          } catch (Exception e) {
               log.error("Failed to load hibernate.properties: {}", e.getMessage(), e);
               throw new RuntimeException("Failed to load hibernate.properties", e);
          }
          log.info("Hibernate properties successfully loaded");
          return props;
     }

     // DAO
     public UserDao userDao() {
          return new UserDaoImpl();
     }

     // Services
     public UserService userService() {
          UserServiceImpl impl = new UserServiceImpl(userDao());
          return serviceFactory.create(UserService.class, impl);
     }

     // UI
     public ConsoleMenu consoleMenu() {
          return new ConsoleMenu(
                  userService(),
                  new ErrorHandler(),
                  new ConsoleRenderer(),
                  new InputValidator()
          );
     }
}


