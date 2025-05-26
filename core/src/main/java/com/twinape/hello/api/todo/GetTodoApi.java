package com.twinape.hello.api.todo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twinape.facade.IApi;
import com.twinape.facade.IRequest;
import com.twinape.facade.annotation.RegisterIApi;
import com.twinape.hello.repo.Todo.Todo;
import com.twinape.hello.repo.Todo.TodoRepo;
import java.util.List;
import java.util.concurrent.CompletionStage;

@Singleton

@RegisterIApi(method = "http.get", endpoint = "todo/get", tag = "public")
final class GetTodoApi implements IApi<IRequest> {

    private final TodoRepo todoRepo;

    @Inject
    GetTodoApi(TodoRepo todoRepo) {
        this.todoRepo = todoRepo;
    }

    @Override
    public CompletionStage<List<Todo>> handle(IRequest request) throws Exception {
        return todoRepo.getAllTodo();
    }
}
