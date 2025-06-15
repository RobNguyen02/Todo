package com.twinape.hello.app;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.twinape.common.Fulfilled;
import com.twinape.common.comp.AutoStopLifeCycle;
import com.twinape.facade.http.TwaHttpServer;
import io.vertx.core.Vertx;
import lombok.AllArgsConstructor;

import java.util.concurrent.CompletableFuture;

@Singleton
@AllArgsConstructor(onConstructor = @__(@Inject))
public class HelloTwinApeApp extends AutoStopLifeCycle {

    private final Vertx vertx;

    @Named("public-http-server")
    private final TwaHttpServer httpServer;

    @Override
    protected void doStart(CompletableFuture<Void> startFuture) throws Exception {
        vertx.deployVerticle(httpServer)
                .<Void>mapEmpty()
                .toCompletionStage()
                .handle(Fulfilled::of)
                .thenAccept(ff -> ff.forward(startFuture));
    }

}
