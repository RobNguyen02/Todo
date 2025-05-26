package com.twinape.hello.repo.Todo;
import java.util.List;
import java.util.concurrent.CompletionStage;

public interface TodoRepo {
    CompletionStage<Long> createtodo( String title, String descr, boolean is_complete);

    CompletionStage<List<Todo>> getAllTodo();

    CompletionStage<Void> updateTodo(int id, String title, String descr, boolean is_complete);

    CompletionStage<Void> deleteTodo(int id);

    CompletionStage<Todo> getTodo(int id);

    CompletionStage<Void> checkdoneTodo(int id);


}
