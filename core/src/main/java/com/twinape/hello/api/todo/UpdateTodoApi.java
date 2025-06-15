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
@RegisterIApi(method = "http.put", endpoint = "todo/update", tag = "public")
public final class UpdateTodoApi implements IApi<IRequest> {

    private final TodoRepo todoRepo;

    @Inject
    public UpdateTodoApi(TodoRepo todoRepo) {

        this.todoRepo = todoRepo;
    }

    @Getter
    @Builder
    @ToString
    @Jacksonized
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static final class UpdateTodoRequest {
        @NonNull
        Integer id;
        String title;
        String descr;
        boolean is_complete;
    }

    @Override
    public CompletionStage<?> handle(IRequest request) throws Exception {
        var create = request.getBodyAs(UpdateTodoRequest.class);
        var id = create.id;
        var title = create.title;
        var description = create.descr;
        var isComplete = create.is_complete;

        return todoRepo.updateTodo(id, title, description, isComplete)
                .thenApply(v -> Map.of("status", 200,
                        "message", "Update todo with id: " + id
                ));

    }
}