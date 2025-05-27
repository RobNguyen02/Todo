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
@RegisterIApi(method = "http.put", endpoint = "todo/checkdone", tag = "public")
final class CheckDoneTodoApi implements IApi<IRequest> {

    private final TodoRepo todoRepo;

    @Inject
    public CheckDoneTodoApi(TodoRepo todoRepo) {

        this.todoRepo = todoRepo;
    }

    @Getter
    @Builder
    @ToString
    @Jacksonized
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class CheckDoneTodoRequest {
        @NonNull
        Integer id;
    }

    @Override
    public CompletionStage<?> handle(IRequest request) throws Exception {
        var checkdone = request.getBodyAs(CheckDoneTodoRequest.class);
        var id = checkdone.id;
        return todoRepo.checkdoneTodo(id)
                .thenApply(v -> Map.of("message", "Check done to do with id: " + id));

    }
}