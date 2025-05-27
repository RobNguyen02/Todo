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

import java.util.Map;
import java.util.concurrent.CompletionStage;


@Singleton
@RegisterIApi(method = "http.delete", endpoint = "todo/wtd/delete", tag = "public")
final class DeleteWtdApi implements IApi<IRequest> {

    private final WhattodoRepo whattodoRepo;

    @Inject
    public DeleteWtdApi(WhattodoRepo whattodoRepo) {

        this.whattodoRepo = whattodoRepo;
    }

    @Getter
    @Builder
    @ToString
    @Jacksonized
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class DeleteWhattodoRequest {
        @NonNull
        Integer id;
    }

    @Override
    public CompletionStage<?> handle(IRequest request) throws Exception {
        var delete = request.getBodyAs(DeleteWhattodoRequest.class);
        var id = delete.id;
        return whattodoRepo.deleteWhattodo(id)
                .thenApply(v -> Map.of("message", "Deleted success whattodo with ID: " + id));
    }
}