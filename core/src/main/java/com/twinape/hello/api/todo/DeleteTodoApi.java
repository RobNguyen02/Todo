package com.twinape.hello.api.todo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twinape.facade.IApi;
import com.twinape.facade.IRequest;
import com.twinape.facade.annotation.RegisterIApi;
import com.twinape.hello.repo.Todo.TodoRepo;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;
import java.util.Map;
import java.util.concurrent.CompletionStage;


@Singleton
@RegisterIApi(method = "http.delete", endpoint = "todo/delete", tag = "public")
final class DeleteTodoApi implements IApi<IRequest> {

    private final TodoRepo todoRepo;

    @Inject
    public DeleteTodoApi(TodoRepo todoRepo) {

        this.todoRepo = todoRepo;
    }

    @Getter
    @Builder
    @ToString
    @Jacksonized
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class DeleteTodoRequest {
        @NonNull
       Integer id;
    }

    @Override
    public CompletionStage<?> handle(IRequest request) throws Exception {
        var delete = request.getBodyAs(DeleteTodoRequest.class);
        var id = delete.id;
        return todoRepo.deleteTodo(id)
                .thenApply(v -> Map.of("message", "Deleted todo with ID: " + id));
    }
}