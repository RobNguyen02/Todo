package com.twinape.hello.mysql;


import com.google.inject.Inject;
import com.twinape.common.mysql.extern.MySqlClient;

import io.vertx.mysqlclient.MySQLClient;
import io.vertx.sqlclient.PropertyKind;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class AbstractMysqlRepo {

    protected static final PropertyKind<Long> LAST_INSERTED_ID = MySQLClient.LAST_INSERTED_ID;

    @Inject
    @Getter(AccessLevel.PROTECTED)
    private MySqlClient mysqlClient;
}
