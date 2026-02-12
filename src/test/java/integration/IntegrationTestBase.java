package integration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import config.HibernateUtil;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.Properties;

public abstract class IntegrationTestBase {

    protected static PostgreSQLContainer<?> postgres;
    protected static SessionFactory sessionFactory;

    @BeforeAll
    static void init() {
        postgres = new PostgreSQLContainer<>("postgres:18.1-alpine3.23")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        postgres.start();

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(postgres.getJdbcUrl());
        cfg.setUsername(postgres.getUsername());
        cfg.setPassword(postgres.getPassword());
        DataSource ds = new HikariDataSource(cfg);

        Flyway.configure()
                .dataSource(ds)
                .locations("classpath:db/migration")
                .load()
                .migrate();

        Properties props = new Properties();
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.setProperty("hibernate.hbm2ddl.auto", "validate");

        sessionFactory = HibernateUtil.buildSessionFactory(props, ds);
    }

    @AfterAll
    static void shutdown() {
        postgres.stop();
    }
}

