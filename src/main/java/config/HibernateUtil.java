package config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.sql.DataSource;
import java.util.Properties;


public class HibernateUtil {

    public static SessionFactory buildSessionFactory(Properties properties, DataSource dataSource) {

        properties.put(org.hibernate.cfg.AvailableSettings.DATASOURCE, dataSource);

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(properties)
                .build();

        MetadataSources sources = new MetadataSources(registry);
        Metadata metadata = sources.buildMetadata();

        return metadata.buildSessionFactory();
    }
}





