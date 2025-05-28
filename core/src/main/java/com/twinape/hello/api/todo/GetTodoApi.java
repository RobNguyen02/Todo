package com.twinape.hello.api.todo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twinape.facade.IApi;
import com.twinape.facade.IRequest;
import com.twinape.facade.annotation.RegisterIApi;
import com.twinape.hello.repo.Todo.Todo;
import com.twinape.hello.repo.Todo.TodoRepo;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Singleton
@RegisterIApi(method = "http.get", endpoint = "todo/get", tag = "public")
public final class GetTodoApi implements IApi<IRequest> {

    private final TodoRepo todoRepo;

    @Inject
    public GetTodoApi(TodoRepo todoRepo) {
        this.todoRepo = todoRepo;
    }

    @Getter
    @Builder
    @ToString
    @Jacksonized
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static final class GetTodoRequest {
        @NonNull
        Integer limit;
        @NonNull
        Integer offset;

    }


    @Override
    public CompletionStage<List<Todo>> handle(IRequest request) throws Exception {
        var lo = request.getBodyAs(GetTodoRequest.class);
        var limit = lo.limit;
        var offset = lo.offset;
        return todoRepo.getAllTodo(limit,offset);
    }
}
