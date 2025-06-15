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
@RegisterIApi(method = "http.post", endpoint = "todo/create", tag = "public")
public final class CreateTodoApi implements IApi<IRequest> {
    private final KafkaTodoRsyncProducer kafkaProducer;

    private final TodoRepo todoRepo;

    @Inject
    public CreateTodoApi(TodoRepo todoRepo) {
        this.todoRepo = todoRepo;
    }


    @Getter
    @Builder
    @ToString
    @Jacksonized
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static final class CreateTodoRequest {
        @NonNull
        String title;
        String descr;
        boolean is_complete;
    }

    @Override
    public CompletionStage<?> handle(IRequest request) throws Exception {
        var create = request.getBodyAs(CreateTodoRequest.class);
        var title = create.title;
        var description = create.descr;
        var isComplete = create.is_complete;
        return todoRepo.createtodo(title, description, isComplete)
                .thenApply(v -> Map.of("message", "Create success todo with title: " + title));

    }
}