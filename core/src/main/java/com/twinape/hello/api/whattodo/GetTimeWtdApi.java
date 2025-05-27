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

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.concurrent.CompletionStage;

@Singleton
@RegisterIApi(method = "http.get", endpoint = "todo/wtd/search/time", tag = "public")
final class GetTimeWtdApi implements IApi<IRequest> {

    private final WhattodoRepo whattodorepo;

    @Inject
    public GetTimeWtdApi(WhattodoRepo whattodorepo) {
        this.whattodorepo = whattodorepo;
    }

    @Getter
    @Builder
    @ToString
    @Jacksonized
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class GetTimeWtdRequest {
        @NonNull
        LocalDateTime starttime;
        LocalDateTime endtime;
        Integer limit;
        Integer offset;
        @Nullable
        Integer idtodo;

    }

    @Override
    public CompletionStage<?> handle(IRequest request) throws Exception {
        var gettimewtd = request.getBodyAs(GetTimeWtdRequest.class);
        var starttime = gettimewtd.starttime;
        var endtime = gettimewtd.endtime;
        var idtodo = gettimewtd.idtodo;
        var limit = gettimewtd.limit;
        var offset = gettimewtd.offset;
        return whattodorepo.gettimeWhattodo(starttime, endtime, idtodo, limit, offset);
    }
}
