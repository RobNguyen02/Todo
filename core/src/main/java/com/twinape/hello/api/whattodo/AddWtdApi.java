package com.twinape.hello.api.whattodo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twinape.facade.IApi;
import com.twinape.facade.IRequest;
import com.twinape.facade.annotation.RegisterIApi;
import com.twinape.hello.repo.Whattodo.WhattodoRepo;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


@Singleton
@RegisterIApi(method = "http.post", endpoint = "todo/wtd/add", tag = "public")
public final class AddWtdApi implements IApi<IRequest> {

    private final WhattodoRepo whattodoRepo;

    @Inject
    public AddWtdApi(WhattodoRepo whattodoRepo) {

        this.whattodoRepo = whattodoRepo;
    }


    @Getter
    @Builder
    @ToString
    @Jacksonized
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static final class AddWtdRequest {
        @NonNull
        String content;

        LocalDateTime starttime;

        LocalDateTime endtime;

        Integer idtodo;
    }

    @Override
    public CompletionStage<?> handle(IRequest request) throws Exception {

        var add = request.getBodyAs(AddWtdRequest.class);
        var content = add.content;
        var startTime = add.starttime;
        var endTime = add.endtime;
        var idtodo = add.idtodo;

        if (add.content == null || add.content.isBlank()) {
            return CompletableFuture.completedFuture(Map.of("error", "Content is required"));
        }
        if (add.starttime.isAfter(add.endtime)) {
            return CompletableFuture.completedFuture(Map.of("error","End time must be after start time"));
        }

        return whattodoRepo.addWhattodo(content, startTime, endTime, idtodo)
                .thenApply(v -> Map.of("message", "Add success what to do with content: " + content));
    }
}