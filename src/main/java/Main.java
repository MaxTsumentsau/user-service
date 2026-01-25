import config.DataSourceFactory;
import config.HibernateUtil;
import io.github.cdimascio.dotenv.Dotenv;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;

import javax.sql.DataSource;
import java.util.Properties;

public class Main {
     public static void main(String[] args) {
          Dotenv env = Dotenv.load();

          DataSource dataSource = DataSourceFactory.create(env);

          Flyway flyway = Flyway.configure()
                  .dataSource(dataSource)
                  .locations("classpath:db/migration")
                  .load();
          flyway.migrate();

          Properties props = new Properties();
          try (var stream = Main.class.getClassLoader().getResourceAsStream("hibernate.properties")) {
               props.load(stream);
          } catch (Exception e) {
               throw new RuntimeException("Failed to load hibernate.properties", e);
          }

          SessionFactory sessionFactory = HibernateUtil.buildSessionFactory(props, dataSource);

          sessionFactory.close();
     }
}


