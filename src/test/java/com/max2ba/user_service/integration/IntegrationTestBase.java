package com.max2ba.user_service.integration;

import com.max2ba.user_service.UserServiceApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = UserServiceApplication.class)
public abstract class IntegrationTestBase {

     @Container
     private static final PostgreSQLContainer<?> postgres =
             new PostgreSQLContainer<>("postgres:18.1-alpine3.23")
                     .withDatabaseName("testdb")
                     .withUsername("test")
                     .withPassword("test");

     @DynamicPropertySource
     static void configureProperties(DynamicPropertyRegistry registry) {
          registry.add("spring.datasource.url", postgres::getJdbcUrl);
          registry.add("spring.datasource.username", postgres::getUsername);
          registry.add("spring.datasource.password", postgres::getPassword);
     }
}

