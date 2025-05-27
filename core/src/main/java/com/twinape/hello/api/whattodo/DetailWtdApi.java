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
import java.util.concurrent.CompletionStage;

@Singleton
@RegisterIApi(method = "http.get", endpoint = "todo/wtd/detail-all", tag = "public")
final class DetailWtdApi implements IApi<IRequest> {

    private final WhattodoRepo whattodoRepo;

    @Inject
    public DetailWtdApi(WhattodoRepo whattodoRepo) {
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
        int limit;
        int offset;
    }

    @Override
    public CompletionStage<?> handle(IRequest request) throws Exception {
        var set = request.getBodyAs(CreateWtdRequest.class);
        var limit = set.limit;
        var offset = set.offset;
        return whattodoRepo.detailWhattodo(limit, offset);
    }
}
