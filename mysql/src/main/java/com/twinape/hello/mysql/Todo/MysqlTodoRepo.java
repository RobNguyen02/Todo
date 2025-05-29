package com.twinape.hello.mysql.Todo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twinape.common.exception.NotFoundException;
import com.twinape.common.sql.SqlQuery;
import com.twinape.common.sql.extern.MultiRows;
import com.twinape.hello.mysql.AbstractMysqlRepo;
import com.twinape.hello.repo.Todo.Todo;
import com.twinape.hello.repo.Todo.TodoRepo;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public final class MysqlTodoRepo extends AbstractMysqlRepo implements TodoRepo {

    @Override
    public CompletionStage<Long> createtodo(String title, String descr, boolean is_complete) {
        var query = SqlQuery.of("""
                INSERT INTO todo (title, descr, is_complete)
                VALUES (?, ?, ?)
                """).withArgs(title, descr, is_complete);
        return getMysqlClient().executeThenGet(query) //
                .thenApply(MultiRows::getRows) //
                .thenApply(row -> row.property(LAST_INSERTED_ID));
    }

    @Override
    public CompletionStage<List<Todo>> getAllTodo(int limit, int offset) {
        var query = SqlQuery.of("""
                SELECT id, title, descr, is_complete
                FROM todo
                LIMIT ? OFFSET ?
                """).withArgs(limit, offset);
        return getMysqlClient().executeThenGet(query)
                .thenApply(rows -> {
                    List<Todo> todos = rows.mapTo(Todo.class);
                    if (todos.isEmpty()) {
                        throw new NotFoundException("No todo found");
                    }
                    return todos;
                });
    }

    @Override
    public CompletionStage<Todo> getTodo(int id) {
        var query = SqlQuery.of("""
                SELECT id, title, descr, is_complete
                FROM todo
                WHERE id = ?
                """).withArg(id);

        return getMysqlClient().executeThenGetFirst(query)
                .thenCompose(row -> {
                    Todo todo = row.mapTo(Todo.class);
                    if (todo == null) {
                        return CompletableFuture.failedStage(new NotFoundException("Todo not found"));
                    }
                    return CompletableFuture.completedStage(todo);
                });
    }

    @Override
    public CompletionStage<Void> checkdoneTodo(int id) {
        var query = SqlQuery.of("""
                UPDATE todo
                SET is_complete = true
                WHERE id = ?
                """).withArg(id);
        return getMysqlClient().executeThenClose(query);
    }


    @Override
    public CompletionStage<Void> updateTodo(int id, String title, String descr, boolean is_complete) {
        var query = SqlQuery.of("""
                UPDATE todo
                SET title = ?, descr = ?, is_complete = ?
                WHERE id = ?
                """).withArgs(title, descr, is_complete, id);

        return getMysqlClient().executeThenClose(query);
    }

    @Override
    public CompletionStage<Void> deleteTodo(int id) {
        var query = SqlQuery.of("""
                DELETE FROM todo
                WHERE id = ?
                """)
                .withArg(id);

        return getMysqlClient().executeThenClose(query);
    }

}
