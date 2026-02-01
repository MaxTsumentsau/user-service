package config;

import entity.User;
import listener.UserCrudListener;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;

import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
public class HibernateUtil {

     public static SessionFactory buildSessionFactory(Properties properties, DataSource dataSource) {

          properties.put(org.hibernate.cfg.AvailableSettings.DATASOURCE, dataSource);

          StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                  .applySettings(properties)
                  .build();

          MetadataSources sources = new MetadataSources(registry);
          sources.addAnnotatedClass(User.class);

          Metadata metadata = sources.getMetadataBuilder().build();

          SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();

          EventListenerRegistry listenerRegistry = sessionFactory
                  .unwrap(SessionFactoryImplementor.class)
                  .getServiceRegistry()
                  .getService(EventListenerRegistry.class);

          UserCrudListener listener = new UserCrudListener();
          if (listenerRegistry != null) {
               listenerRegistry.appendListeners(EventType.POST_INSERT, listener);
               listenerRegistry.appendListeners(EventType.POST_UPDATE, listener);
               listenerRegistry.appendListeners(EventType.POST_DELETE, listener);
               listenerRegistry.appendListeners(EventType.POST_LOAD, listener);
          }

          return sessionFactory;
     }
}





