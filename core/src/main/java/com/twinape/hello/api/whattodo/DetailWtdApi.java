package com.twinape.hello.api.whattodo;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twinape.facade.IApi;
import com.twinape.facade.IRequest;
import com.twinape.facade.annotation.RegisterIApi;
import com.twinape.hello.repo.Whattodo.WhattodoRepo;
import lombok.*;


import java.util.concurrent.CompletionStage;

@Singleton
@RegisterIApi(method = "http.get", endpoint = "todo/wtd/detail-all", tag = "public")
final class DetailWtdApi implements IApi<IRequest> {

    private final WhattodoRepo whattodoRepo;

    @Inject
    public DetailWtdApi(WhattodoRepo whattodoRepo) {
        this.whattodoRepo = whattodoRepo;
    }



    @Override
    public CompletionStage<?> handle(IRequest iRequest) throws Exception {
        return whattodoRepo.detailWhattodo();
    }
}
