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
import java.util.concurrent.CompletionStage;


@Singleton
@RegisterIApi(method = "http.post", endpoint = "todo/wtd/add", tag = "public")
final class AddWtdApi implements IApi<IRequest> {

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
    private static final class CreateWtdRequest {
        @NonNull
        String content;

        LocalDateTime starttime;

        LocalDateTime endtime;

        Integer idtodo;
    }

    @Override
    public CompletionStage<?> handle(IRequest request) throws Exception {

        var create = request.getBodyAs(CreateWtdRequest.class);
        var content = create.content;
        var startTime = create.starttime;
        var endTime = create.endtime;
        var idtodo = create.idtodo;
        if (create.starttime == null || create.endtime == null) {
            throw new IllegalArgumentException("Start time and end time are required");
        }

        if (create.starttime.isAfter(create.endtime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        return whattodoRepo.addWhattodo(content, startTime, endTime, idtodo)
                .thenApply(v -> Map.of("message", "Add success what to do with content: " + content));
    }
}