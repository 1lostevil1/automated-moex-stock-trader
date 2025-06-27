package org.example.controller.auth;

import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.kafka.shaded.io.opentelemetry.proto.trace.v1.Status;
import org.example.models.request.AuthTokenRequest;
import org.example.models.request.RegistrationRequest;
import org.example.postgres.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.Assert;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
@Testcontainers
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthController controller;

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("TraderDB")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("jwt.secret", () -> "test-secret-value-bluy-ocheni-clozno");
        registry.add("jwt.lifetime", () -> "900000");

        // Указываем путь к changelog в другом модуле
        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/master.xml");
        registry.add("spring.liquibase.enabled", () -> "true");
    }

    @Test
    @Order(1)
    void successRegisterTest(){
        RegistrationRequest request = new RegistrationRequest("Test","Test","P@ssw0rd");
        var res = controller.register(request);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        var entity = userRepository.getByUsername("Test");
        assertTrue(entity.isPresent());
    }

    @Test
    @Order(2)
    void UserAlreadyExistRegisterTest(){
        RegistrationRequest request = new RegistrationRequest("Test","Test","P@ssw0rd");
        var res = controller.register(request);
        assertEquals(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        var entity = userRepository.getByUsername("Test");
        assertTrue(entity.isPresent());
    }

    @Test
    @Order(3)
    void successLoginTest(){
        AuthTokenRequest request = new AuthTokenRequest("Test","P@ssw0rd");
        var res = controller.createAuthToken(request);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @Order(4)
    void WrongLoginORPasswordLoginTest(){
        AuthTokenRequest request = new AuthTokenRequest("Test","P@ssw0rd1");
        var res = controller.createAuthToken(request);
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }
}