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
import java.util.concurrent.CompletionStage;

@Singleton
@RegisterIApi(method = "http.get", endpoint = "todo/wtd/get", tag = "public")
final class GetWtdApi implements IApi<IRequest> {

    private final WhattodoRepo whattodorepo;

    @Inject
    public GetWtdApi(WhattodoRepo whattodorepo) {
        this.whattodorepo = whattodorepo;
    }

    @Getter
    @Builder
    @ToString
    @Jacksonized
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class GetWhattodoRequest {
        @NonNull
        Integer idtodo;

    }
    @Override
    public CompletionStage<?> handle(IRequest request) throws Exception {
        var getbyid = request.getBodyAs(GetWhattodoRequest.class);
        var idtodo = getbyid.idtodo;
        return whattodorepo.getWhattodo(idtodo);
    }
}
