package com.twinape.hello.app.boots;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.twinape.common.mysql.extern.MySqlClient;
import com.twinape.common.mysql.supplier.MySqlConnectionSupplier;
import com.twinape.facade.http.TwaHttpServer;
import com.twinape.facade.http.config.HttpStatusCodeMapping;
import com.twinape.facade.http.response.DefaultJsonHttpResponder;
import com.twinape.facade.registry.IApiRegistry;
import com.twinape.hello.app.HelloTwinApeAppConfig;
import com.twinape.hello.mysql.Todo.MysqlTodoRepo;
import com.twinape.hello.mysql.Whattodo.MysqlWhattodoRepo;
import com.twinape.hello.repo.Todo.TodoRepo;
import com.twinape.hello.repo.Whattodo.WhattodoRepo;
import io.micrometer.core.instrument.MeterRegistry;
import io.vertx.core.Vertx;

final class ApiModule extends AbstractModule {

    @Provides
    @Singleton
    MySqlClient mysqlClient(HelloTwinApeAppConfig appConfig, Vertx vertx) {
        var mysqlUri = appConfig.getMySql().getUri();
        var connSupplier = MySqlConnectionSupplier.from(mysqlUri, vertx);
        return new MySqlClient(connSupplier);
    }

    @Provides
    @Singleton
    IApiRegistry apiRegistry(Injector injector) {
        return IApiRegistry.scan(injector);
    }

    @Provides
    @Singleton
    HttpStatusCodeMapping httpStatusCodeMapping(Injector injector) {
        return HttpStatusCodeMapping.scanAndCreate();
    }

    @Provides
    @Singleton
    @Named("public-http-server")
    TwaHttpServer httpServer(HelloTwinApeAppConfig appConfig, IApiRegistry apiRegistry, HttpStatusCodeMapping codeMapping,
                             MeterRegistry meterRegistry) {
        var config = appConfig.getPublicHttp();
        var responder = new DefaultJsonHttpResponder(codeMapping);
        return TwaHttpServer.builder() //
                .responder(responder) //
                .apiRegistry(apiRegistry) //
                .meterRegistry(meterRegistry) //
                .config(config.toBuilder() //
                        .tags("public") //
                        .build()) //
                .build();
    }
//
    @Override
    public void configure() {
        bind(TodoRepo.class).to(MysqlTodoRepo.class).in(Singleton.class);
        bind(WhattodoRepo.class).to(MysqlWhattodoRepo.class).in(Singleton.class);
    }
}
