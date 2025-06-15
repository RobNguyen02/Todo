package com.twinape.hello.mysql.Whattodo;

import com.twinape.common.mysql.extern.MySqlClient;
import com.twinape.common.mysql.supplier.MySqlConnectionSupplier;
import com.twinape.common.sql.SqlQuery;
import com.twinape.common.uri.ParsedUri;
import com.twinape.hello.mysql.Todo.MysqlTodoRepo;
import com.twinape.hello.mysql.Todo.MysqlTodoRepoTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;


@ExtendWith(VertxExtension.class)
@Testcontainers
public class MysqlWhattodoRepoTest {
    private static final Logger logger = LoggerFactory.getLogger(MysqlTodoRepoTest.class);
    private static Vertx vertx;
    private static MySqlClient sqlClient;
    private static MysqlTodoRepo todoRepo;

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("todo_test_db")
            .withUsername("test")
            .withPassword("test");


    @BeforeAll
    static void setUp(VertxTestContext testContext) {
        vertx = Vertx.vertx();
        String mysqlUrl = String.format("postgres-steady://%s:%s@%s:%d/%s",
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword(),
                mysqlContainer.getHost(),
                mysqlContainer.getMappedPort(3306),
                mysqlContainer.getDatabaseName()
        );

        ParsedUri mysqlUri = ParsedUri.parse(mysqlUrl);

        var connSupplier = MySqlConnectionSupplier.from(mysqlUri, vertx);
        sqlClient = new MySqlClient(connSupplier);
        todoRepo = new MysqlTodoRepo();

        // Inject sqlClient vào todoRepo sử dụng reflection
        try {
            Field sqlClientField = todoRepo.getClass().getSuperclass().getDeclaredField("mysqlClient");
            sqlClientField.setAccessible(true);
            sqlClientField.set(todoRepo, sqlClient);
        } catch (Exception e) {
            testContext.failNow(e);
            return;
        }


// Create tables
        var createTableQueries = new SqlQuery[]{
                // First query: Create todo_table
                SqlQuery.of("""
                            CREATE TABLE IF NOT EXISTS todo (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                title VARCHAR(50) NOT NULL,
                                descr VARCHAR(255),
                                is_complete BOOLEAN DEFAULT FALSE,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                            )
                        """),

                // Second query: Create whattodo table
                SqlQuery.of("""
                            CREATE TABLE IF NOT EXISTS whattodo (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                content VARCHAR(255) NOT NULL,
                                starttime DATETIME NOT NULL,
                                endtime DATETIME NOT NULL,
                                idtodo BIGINT NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                CONSTRAINT fk_todo 
                                    FOREIGN KEY (idtodo) 
                                    REFERENCES todo(id)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,
                                CONSTRAINT check_time 
                                    CHECK (endtime > starttime)
                            )
                        """)
        };

//// Execute queries in sequence
//        CompletableFuture<Void> setup = CompletableFuture.completedFuture(null);
//        for (SqlQuery query : createTableQueries) {
//            setup = setup.thenCompose(v -> sqlClient.execute(query));
//        }
//
//        setup.thenRun(() -> {
//                    logger.info("Tables created successfully");
//                    testContext.completeNow();
//                })
//                .exceptionally(err -> {
//                    logger.error("Failed to create tables", err);
//                    testContext.failNow(err);
//                    return null;
//                });

    }
    @AfterAll
    static void tearDown(VertxTestContext testContext) {
        String dropTables = """
            DROP TABLE IF EXISTS push_allowance_group;
            DROP TABLE IF EXISTS push_group;
            DROP TABLE IF EXISTS push_allowance;
            DROP TABLE IF EXISTS blena;
            DROP SEQUENCE IF EXISTS push_group_id_seq;
            DROP SEQUENCE IF EXISTS push_allowance_id_seq;
            DROP SEQUENCE IF EXISTS blena_id_seq;
            """;

        sqlClient.execute(dropTables)
                .thenRun(() -> {
                    logger.info("Tables and sequences dropped successfully");
                    sqlClient.executeThenClose(SqlQuery.of("SELECT 1"))
                            .thenRun(() -> {
                                vertx.close().onComplete(ar -> {
                                    if (ar.succeeded()) {
                                        logger.info("Vertx closed successfully");
                                        testContext.completeNow();
                                    } else {
                                        logger.error("Failed to close Vertx", ar.cause());
                                        testContext.failNow(ar.cause());
                                    }
                                });
                            });
                })
                .exceptionally(err -> {
                    logger.error("Failed to drop tables and sequences", err);
                    testContext.failNow(err);
                    return null;
                });
    }
}

