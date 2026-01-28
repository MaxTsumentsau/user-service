package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

@Slf4j
public class DataSourceFactory {
     public final static int MAXIMUM_POOL_SIZE=10;
     public final static int MINIMUM_IDLE=2;
     public final static long IDLE_TIMEOUT=60000;
     public final static long MAX_LIFETIME=1800000;
     public final static long CONNECTION_TIMEOUT=30000;

     public static DataSource create(Dotenv env) {
          log.debug("Configuring HikariCP connection pool...");

          HikariConfig config = new HikariConfig();
          config.setJdbcUrl(env.get("DB_URL"));
          config.setUsername(env.get("DB_USERNAME"));
          config.setPassword(env.get("DB_PASSWORD"));

          config.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
          config.setMinimumIdle(MINIMUM_IDLE);
          config.setIdleTimeout(IDLE_TIMEOUT);
          config.setMaxLifetime(MAX_LIFETIME);
          config.setConnectionTimeout(CONNECTION_TIMEOUT);

          log.debug("""
HikariCP configured:
  jdbcUrl={}
  maxPoolSize={}
  minIdle={}
  idleTimeoutMs={}
  maxLifetimeMs={}
  connectionTimeoutMs={}
""",
                  env.get("DB_URL"),
                  config.getMaximumPoolSize(),
                  config.getMinimumIdle(),
                  config.getIdleTimeout(),
                  config.getMaxLifetime(),
                  config.getConnectionTimeout()
          );
          return new HikariDataSource(config);
     }
}



