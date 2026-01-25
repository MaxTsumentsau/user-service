package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import javax.sql.DataSource;

public class DataSourceFactory {
    public static DataSource create(Dotenv env) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(env.get("DB_URL"));
        config.setUsername(env.get("DB_USERNAME"));
        config.setPassword(env.get("DB_PASSWORD"));
        config.setDriverClassName("org.postgresql.Driver");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(30000);

        return new HikariDataSource(config);
    }
}



