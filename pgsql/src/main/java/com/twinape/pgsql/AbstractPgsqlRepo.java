package com.twinape.pgsql;


import com.google.inject.Inject;
import com.twinape.common.pgpool.extern.PgClient;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class AbstractPgsqlRepo {


    @Inject
    @Getter(AccessLevel.PROTECTED)
    private PgClient pgsqlClient;
}
