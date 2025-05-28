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
@RegisterIApi(method = "http.put", endpoint = "todo/wtd/update", tag = "public")
public final class UpdateWtdApi implements IApi<IRequest> {

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
    public static final class UpdateWtdRequest {
        @NonNull
        Integer id;

        @NonNull
        String content;

        LocalDateTime starttime;

        LocalDateTime endtime;
    }

    @Override
    public CompletionStage<?> handle(IRequest request) throws Exception {
        var update = request.getBodyAs(UpdateWtdRequest.class);
        var id = update.id;
        var content = update.content;
        var starttime = update.starttime;
        var endtime = update.endtime;

        if (update.content == null || update.content.isBlank()) {
            return CompletableFuture.completedFuture(Map.of("error", "content is required"));
        }
        return whattodoRepo.updateWhattodo(id, content, starttime, endtime)
                .thenApply(v -> Map.of("message", "Update todo with id: " + id));

    }
}