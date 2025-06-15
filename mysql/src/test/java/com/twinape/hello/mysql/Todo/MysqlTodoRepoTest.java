package com.twinape.hello.mysql.Todo;

import com.twinape.common.exception.NotFoundException;
import com.twinape.common.mysql.extern.MySqlClient;
import com.twinape.common.mysql.supplier.MySqlConnectionSupplier;
import com.twinape.common.sql.SqlQuery;
import com.twinape.common.uri.ParsedUri;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(VertxExtension.class)
@Testcontainers
public class MysqlTodoRepoTest {
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

        String mysqlUrl = String.format("mysql-steady://%s:%s@%s:%d/%s",
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

        // Create table
        var createTableQuery = SqlQuery.of("""
                    CREATE TABLE IF NOT EXISTS todo (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        title VARCHAR(255) NOT NULL,
                        descr TEXT,
                        is_complete BOOLEAN DEFAULT FALSE )
                """);

        sqlClient.execute(createTableQuery)
                .thenRun(() -> {
                    logger.info("Table created successfully");
                    testContext.completeNow();
                })
                .exceptionally(err -> {
                    logger.error("Failed to create table", err);
                    testContext.failNow(err);
                    return null;
                });
    }

    @Test
    @DisplayName("""
            Given: A new todo data_
            When: Create new todo data_
            Then: saved data - Should retrievable with correct data _
            """)
    void testCreateTodo(VertxTestContext testContext) {
        String title = "Test Todo";
        String description = "Test Description";
    //    String FakeErrorDescr   = " error descr fake";
        todoRepo.createtodo(title, description, false)
                .thenCompose(id -> {
                    assertNotNull(id);
                    assertTrue(id > 0);
                    return todoRepo.getTodo(id.intValue());
                })
                .thenAccept(todo -> {
                    assertEquals(title, todo.getTitle());
                    assertEquals(description, todo.getDescr());
                    assertFalse(todo.isComplete());
                    testContext.completeNow();
                })
                .exceptionally(err -> {
                    testContext.failNow(err);
                    return null;
                });
    }

    @Test
    @DisplayName("""
            Given: Data in todo in table_
            When: Get all data_
            Then: Try to get all data - Should return all datata given_ 
            """)
    void testGetAllTodos(VertxTestContext testContext) {

        todoRepo.createtodo("Todo 1", "Desc 1", false)
                .thenCompose(id1 -> todoRepo.createtodo("Todo 2", "Desc 2", true))
                    .thenCompose(id2 -> todoRepo.getAllTodo(10, 0))
                    .thenAccept(todos -> {
                        assertNotNull(todos);
                        assertTrue(todos.size() >= 2);
                        assertTrue(todos.stream().anyMatch(todo ->
                                "Todo 1".equals(todo.getTitle()) && "Desc 1".equals(todo.getDescr())
                        ));
                        assertTrue(todos.stream().anyMatch(todo ->
                                "Todo 2".equals(todo.getTitle()) && "Desc 2".equals(todo.getDescr())
                        ));
                        testContext.completeNow();

                    })
                    .exceptionally(err -> {
                        testContext.failNow(err);
                        return null;
                    });
    }

    @Test
    @DisplayName("""
            Given: data todo
            When: update title with new values
            Then: Try to get data updated - Should be correct data_
            """)
    void testUpdateTodo(VertxTestContext testContext) {
        String initialTitle = "Initial title";
        String initialDescr = "Initial Description";
        boolean initalComplete = true;

        String updatedTitle = "Updated title";

        todoRepo.createtodo(initialTitle, initialDescr, initalComplete)
                .thenCompose(id -> {
                    return todoRepo.updateTodo(id.intValue(), updatedTitle, initialDescr, initalComplete)
                            .thenCompose(v -> todoRepo.getTodo(id.intValue()));})
                .thenAccept(todo -> {
                    assertEquals(updatedTitle, todo.getTitle());
                    assertTrue(todo.isComplete());
                    testContext.completeNow();
                })
                .exceptionally(err -> {
                    testContext.failNow(err);
                    return null;
                });
    }

    @Test
    @DisplayName("""
            Given: data todo in database with is_complete is false_
            When: Call checkdoneTodo to mark it as complete_
            Then:  Try to get todo - Should have is_complete is true_ 
            """)
    void testCheckDoneTodo(VertxTestContext testContext) {
        String title = "test check done title";
        String descr = "test check done descr";
        boolean isComplete = false;

        todoRepo.createtodo(title,descr,isComplete)
                .thenCompose(id -> {
                    return todoRepo.checkdoneTodo(id.intValue())
                            .thenCompose(v -> todoRepo.getTodo(id.intValue()));})
                    .thenAccept(todo -> {
                    assertTrue(todo.isComplete());
                    testContext.completeNow();
                })
                .exceptionally(err -> {
                    testContext.failNow(err);
                    return null;
                });
    }


    @Test
    @DisplayName("""
        Given: Create a new todo_
        When: Delete it using deleteTodo()_
        Then: Try to get deleted todo - should throw NotFoundException_
        """)
    void testDeleteTodo(VertxTestContext testContext) {
        todoRepo.createtodo("Todo to delete", "Test delete", false)
                .thenCompose(id -> {
                    return todoRepo.deleteTodo(id.intValue())
                            .thenCompose(v -> {
                                return todoRepo.getTodo(id.intValue())
                                        .handle((todo, err) -> {
                                            assertNotNull(err);
                                            assertTrue(err.getCause() instanceof NotFoundException,
                                                    "Should throw NotFoundException for deleted todo");
                                            testContext.completeNow();
                                            return null;
                                        });
                            });
                })
                .exceptionally(err -> {
                    testContext.failNow(err);
                    return null;
                });
    }
    @AfterAll
    static void tearDown(VertxTestContext testContext) {
        sqlClient.execute("DROP TABLE IF EXISTS todo")
                .thenRun(() -> {
                    logger.info("Table dropped successfully");
                    // Close in reverse order
                    sqlClient.executeThenClose(SqlQuery.of("SELECT 1"))
                            .thenRun(() -> vertx.close().onComplete(v -> testContext.completeNow()));
                })
                .exceptionally(err -> {
                    logger.error("Failed to drop table", err);
                    testContext.failNow(err);
                    return null;
                });
    }
}