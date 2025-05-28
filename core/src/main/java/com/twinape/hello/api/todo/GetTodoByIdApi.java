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

import java.util.concurrent.CompletionStage;

@Singleton
@RegisterIApi(method = "http.get", endpoint = "todo/get/byid", tag = "public")
public final class GetTodoByIdApi implements IApi<IRequest> {

    private final TodoRepo todoRepo;

    @Inject
    public GetTodoByIdApi(TodoRepo todoRepo) {
        this.todoRepo = todoRepo;
    }

    @Getter
    @Builder
    @ToString
    @Jacksonized
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static final class GetTodoByIdRequest {
        @NonNull
        Integer id;

    }

    @Override
    public CompletionStage<?> handle(IRequest request) throws Exception {
        var getbyid = request.getBodyAs(GetTodoByIdRequest.class);
        var id = getbyid.id;
        return todoRepo.getTodo(id);
    }
}
