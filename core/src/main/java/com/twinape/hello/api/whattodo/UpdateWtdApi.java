package com.twinape.hello.api.whattodo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twinape.facade.IApi;
import com.twinape.facade.IRequest;
import com.twinape.facade.annotation.RegisterIApi;
import com.twinape.hello.repo.Todo.TodoRepo;
import com.twinape.hello.repo.Whattodo.WhattodoRepo;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletionStage;


@Singleton
@RegisterIApi(method = "http.put", endpoint = "todo/wtd/update", tag = "public")
final class UpdateWtdApi implements IApi<IRequest> {

    private final WhattodoRepo whattodoRepo;

    @Inject
    public UpdateWtdApi(WhattodoRepo whattodoRepo) {

        this.whattodoRepo = whattodoRepo;
    }

    @Getter
    @Builder
    @ToString
    @Jacksonized
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class UpdateWtdRequest {
        @NonNull
        Integer id;

        String content;

        LocalDateTime starttime;

        LocalDateTime endtime;

        Integer idtodo;
    }

    @Override
    public CompletionStage<?> handle(IRequest request) throws Exception {
        var update = request.getBodyAs(UpdateWtdRequest.class);
        var id = update.id;
        var content = update.content;
        var starttime=  update.starttime;
        var endtime = update.endtime;

        return whattodoRepo.updateWhattodo(id,content,starttime,endtime)
                .thenApply(v -> Map.of("message", "Update todo with id: " + id));

    }
}