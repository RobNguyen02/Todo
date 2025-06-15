package com.twinape.hello.app.boots;


import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.twinape.common.mysql.extern.MySqlClient;
import com.twinape.common.mysql.supplier.MySqlConnectionSupplier;
import com.twinape.hello.app.HelloTwinApeAppConfig;
import com.twinape.hello.mysql.Todo.MysqlTodoRepo;
import com.twinape.hello.mysql.Whattodo.MysqlWhattodoRepo;
import com.twinape.hello.repo.Todo.TodoRepo;
import com.twinape.hello.repo.Whattodo.WhattodoRepo;
import io.vertx.core.Vertx;

public class MysqlModule extends AbstractModule {
//
//    @Provides
//    @Singleton
//    MySqlClient mysqlClient(HelloTwinApeAppConfig appConfig, Vertx vertx) {
//        var mysqlUri = appConfig.getMySql().getUri();
//        var connSupplier = MySqlConnectionSupplier.from(mysqlUri, vertx);
//        return new MySqlClient(connSupplier);
//    }

//    @Override
//    public void configure() {
//        bind(TodoRepo.class).to(MysqlTodoRepo.class).in(Singleton.class);
//        bind(WhattodoRepo.class).to(MysqlWhattodoRepo.class).in(Singleton.class);
//    }
}
