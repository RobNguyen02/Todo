package com.twinape.pgsql.Todo;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twinape.common.exception.NotFoundException;
import com.twinape.common.sql.SqlQuery;
import com.twinape.common.sql.extern.SingleRow;
import com.twinape.hello.repo.Todo.Todo;
import com.twinape.hello.repo.Todo.TodoRepo;
import com.twinape.pgsql.AbstractPgsqlRepo;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class PgsqlTodoRepo extends AbstractPgsqlRepo implements TodoRepo {

    @Override
    public CompletionStage<Long> createtodo(String title, String descr, boolean is_complete) {
        var query = SqlQuery.of("""
            INSERT INTO todo (title, descr, is_complete)
            VALUES ($1, $2, $3)
            RETURNING id
            """).withArgs(title, descr, is_complete);

        return getPgsqlClient().executeThenGetFirst(query)
                .thenApply(SingleRow::getIdLong);
    }

        @Override
        public CompletionStage<List<Todo>> getAllTodo(int limit, int offset) {
            var query = SqlQuery.of("""
                SELECT id, title, descr, is_complete
                FROM todo
                ORDER BY id  -- thêm ORDER BY để đảm bảo thứ tự nhất quán
                LIMIT $1 OFFSET $2
                """).withArgs(limit, offset);

            return getPgsqlClient().executeThenGet(query)
                    .thenApply(rows -> {
                        List<Todo> todos = rows.mapTo(Todo.class);
                        if (todos.isEmpty()) {
                            throw new NotFoundException("No todo found");
                        }
                        return todos;
                    });
        }

        @Override
        public CompletionStage<Void> updateTodo(int id, String title, String descr, boolean is_complete) {
            var query = SqlQuery.of("""
                UPDATE todo
                SET title = $1, descr = $2, is_complete = $3
                WHERE id = $4
                """).withArgs(title, descr, is_complete, id);

            return getPgsqlClient().executeThenClose(query);
        }

        @Override
        public CompletionStage<Void> deleteTodo(int id) {
            var query = SqlQuery.of("""
                DELETE FROM todo
                WHERE id = $1
                """).withArg(id);

            return getPgsqlClient().executeThenClose(query);
        }

        @Override
        public CompletionStage<Todo> getTodo(int id) {
            var query = SqlQuery.of("""
                SELECT id, title, descr, is_complete
                FROM todo
                WHERE id = $1
                """).withArg(id);

            return getPgsqlClient().executeThenGetFirst(query)
                    .thenApply(row -> {
                        if (row == null) {
                            throw new NotFoundException("Todo not found");
                        }
                        return row.mapTo(Todo.class);
                    });
        }

        @Override
        public CompletionStage<Void> checkdoneTodo(int id) {
            var query = SqlQuery.of("""
                UPDATE todo
                SET is_complete = true
                WHERE id = $1
                """).withArg(id);

            return getPgsqlClient().executeThenClose(query);
        }
    }