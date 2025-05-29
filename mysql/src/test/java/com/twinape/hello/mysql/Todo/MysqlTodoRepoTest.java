package com.twinape.hello.mysql.Todo;

import jakarta.inject.Singleton;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
public class MysqlTodoRepoTest {


    @Singleton
    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("todo_test_db")
            .withUsername("test")
            .withPassword("12345678");


    private static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException {
        mysqlContainer.start();
        System.out.println("MySQL container started: " + mysqlContainer.getJdbcUrl());
        connection = DriverManager.getConnection(
                mysqlContainer.getJdbcUrl(),
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword()
        );

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS todos (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            title VARCHAR(255) NOT NULL,
                            completed BOOLEAN DEFAULT FALSE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        )
                    """);
        }
    }

    @Test
    void canEstablishConnectionTest() throws SQLException {
        assertTrue(mysqlContainer.isCreated());
        assertTrue(mysqlContainer.isRunning());

    }

    @Test
    void testConnection() throws SQLException {
        connection = mysqlContainer.createConnection("");
        assertNotNull(connection);
        System.out.println(connection.getSchema());
    }

    @Test
    void testDatabaseConnection() throws SQLException {
        assertTrue(connection.isValid(1), "Database connection should be valid");
    }

    @Test
    void testSelectTodo() throws SQLException {
        String title = "Test Todo";
        try (var stmt = connection.prepareStatement("INSERT INTO todos (title) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
            boolean affectedRows = stmt.execute();
//            assertTrue(affectedRows);
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                assertTrue(rs.next());
                int id = rs.getInt(1);

                try (PreparedStatement selectStmt = connection.prepareStatement(
                        "SELECT * FROM todos WHERE id = ?",
                        Statement.RETURN_GENERATED_KEYS
                )) {
                    selectStmt.setInt(1, id);
                    ResultSet resultSet = selectStmt.executeQuery();
                    assertTrue(resultSet.next());
                    assertEquals(title, resultSet.getString("title"));
                    assertFalse(resultSet.getBoolean("completed"));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test
    void testInsertTodorepo() throws SQLException {
        String title = "Test Todo";
        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO todos (title) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            pstmt.setString(1, title);
            boolean affectedRows = pstmt.execute();
           // assertTrue(affectedRows);


            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                assertTrue(rs.next());
                int id = rs.getInt(1);

                try (PreparedStatement selectStmt = connection.prepareStatement(
                        "SELECT * FROM todos WHERE id = ?"
                )) {
                    selectStmt.setInt(1, id);
                    ResultSet resultSet = selectStmt.executeQuery();
                    assertTrue(resultSet.next());
                    assertEquals(title, resultSet.getString("title"));
                    assertFalse(resultSet.getBoolean("completed"));
                }
            }
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS todos");
        }

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}