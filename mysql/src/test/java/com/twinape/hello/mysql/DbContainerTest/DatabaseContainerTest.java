package com.twinape.hello.mysql.DbContainerTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class DatabaseContainerTest {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("todo_test_db")
            .withUsername("test")
            .withPassword("test");

    private static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException {
        mysqlContainer.start();
        connection = DriverManager.getConnection(
                mysqlContainer.getJdbcUrl(),
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword()
        );
    }

    @Test
    void testContainerRunning() {
        assertTrue(mysqlContainer.isRunning());
    }

    @Test
    void testDatabaseConnection() throws SQLException {
        assertNotNull(connection);
        assertTrue(connection.isValid(1));
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}