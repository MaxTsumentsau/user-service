package config;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.util.Properties;

public class FlywayInitializer {
    public static void migrate(DataSource dataSource, Properties properties) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(properties.getProperty("flyway.locations"))
                .load();
        flyway.migrate();
    }
}
