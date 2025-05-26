package com.twinape.hello.api.whattodo;

import com.fasterxml.jackson.annotation.JsonFormat;
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

import lombok.*;

@Singleton
@RegisterIApi(method = "http.put", endpoint = "todo/wtd/trans", tag = "public")
final class TransfromWtdApi implements IApi<IRequest> {

    private final WhattodoRepo whattodoRepo;

    @Inject
    public TransfromWtdApi(WhattodoRepo whattodoRepo) {

        this.whattodoRepo = whattodoRepo;
    }

    @Getter
    @Builder
    @ToString
    @Jacksonized
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class TransWtdRequest {
        @NonNull
        Integer id;
        Integer idtodo;

    }

    @Override
    public CompletionStage<?> handle(IRequest request) throws Exception {
        var trans = request.getBodyAs(TransWtdRequest.class);
        var id = trans.id;
        var idtodo = trans.idtodo;

        return whattodoRepo.transformWhattodo(id, idtodo)
                .thenApply(v -> Map.of("message", "transform success idtodp to: "+idtodo));

    }
}