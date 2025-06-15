package com.twinape.hello.app.boots;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import com.twinape.common.pgpool.extern.PgClient;
import com.twinape.common.pgpool.supplier.PgConnectionSupplier;
import com.twinape.hello.app.HelloTwinApeAppConfig;

import com.twinape.hello.repo.Todo.TodoRepo;
import com.twinape.hello.repo.Whattodo.WhattodoRepo;
import com.twinape.pgsql.Todo.PgsqlTodoRepo;
import com.twinape.pgsql.Whattodo.PgsqlWhattodoRepo;
import io.vertx.core.Vertx;

public class PosgreModule extends AbstractModule {


    @Provides
    @Singleton
    PgClient pgClient(HelloTwinApeAppConfig appConfig, Vertx vertx) {
        var pgsqlUri = appConfig.getPgSql().getUri();
        var connSupplier = PgConnectionSupplier.from(pgsqlUri, vertx);
        return new PgClient(connSupplier);
    }

    @Override
    public void configure() {
        bind(TodoRepo.class).to(PgsqlTodoRepo.class).in(Singleton.class);
        bind(WhattodoRepo.class).to(PgsqlWhattodoRepo.class).in(Singleton.class);
    }

}
