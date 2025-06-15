package com.twinape.hello.mysql.DbContainerTest;

import com.twinape.common.mysql.extern.MySqlClient;
import com.twinape.common.mysql.supplier.MySqlConnectionSupplier;
import com.twinape.common.sql.SqlQuery;
import com.twinape.common.uri.ParsedUri;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(VertxExtension.class)
@Testcontainers
public class DatabaseContainerTest {



    private static final Logger logger = LoggerFactory.getLogger(DatabaseContainerTest.class);
    private static Vertx vertx;
    private static MySqlClient sqlClient;

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("todo_test_db")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void setUp(VertxTestContext testContext) {
        vertx = Vertx.vertx();

        String mysqlUrl = String.format("mysql-steady://%s:%s@%s:%d/%s",
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword(),
                mysqlContainer.getHost(),
                mysqlContainer.getMappedPort(3306),
                mysqlContainer.getDatabaseName()
        );

        // Khởi tạo MySqlClient
        try {
            var connSupplier = MySqlConnectionSupplier.from(ParsedUri.parse(mysqlUrl), vertx);
            sqlClient = new MySqlClient(connSupplier);
            testContext.completeNow();
        } catch (Exception e) {
            logger.error("Failed to initialize database connection", e);
            testContext.failNow(e);
        }
    }



    @Test
    @DisplayName("Test if MySQL container is running")
    void testContainerRunning() {
        assertTrue(mysqlContainer.isRunning(), "MySQL container should be running");
        assertNotNull(mysqlContainer.getMappedPort(3306), "MySQL port should be mapped");
    }

    @Test
    @DisplayName("Test database connection using MySqlClient")
    void testDatabaseConnection(VertxTestContext testContext) throws SQLException {
        sqlClient.execute(SqlQuery.of("SELECT 1"))
                .thenAccept(result -> {
                    assertNotNull(result, "Query result should not be null");
                    testContext.completeNow();
                })
                .exceptionally(err -> {
                    testContext.failNow(err);
                    return null;
                });
    }
    @AfterAll
    static void tearDown(VertxTestContext testContext) {
        if (sqlClient != null) {
            // Close connections properly
            sqlClient.executeThenClose(SqlQuery.of("SELECT 1"))
                    .thenRun(() -> {
                        vertx.close().onComplete(v -> {
                            logger.info("Database connections closed successfully");
                            testContext.completeNow();
                        });
                    })
                    .exceptionally(err -> {
                        logger.error("Error closing database connections", err);
                        testContext.failNow(err);
                        return null;
                    });
        } else {
            vertx.close().onComplete(v -> testContext.completeNow());
        }
    }
}