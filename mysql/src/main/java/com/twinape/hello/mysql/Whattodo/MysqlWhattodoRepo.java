package com.twinape.hello.mysql.Whattodo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twinape.common.exception.NotFoundException;
import com.twinape.common.sql.SqlQuery;
import com.twinape.common.sql.extern.MultiRows;
import com.twinape.hello.mysql.AbstractMysqlRepo;
import com.twinape.hello.repo.Whattodo.Whattodo;
import com.twinape.hello.repo.Whattodo.WhattodoRepo;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletionStage;


@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class MysqlWhattodoRepo extends AbstractMysqlRepo implements WhattodoRepo {


    @Override
    public CompletionStage<Long> addWhattodo(String content, LocalDateTime starttime, LocalDateTime endtime, int idtodo) {
        var query = SqlQuery.of("""
                INSERT INTO whattodo (content, starttime, endtime, idtodo)
                SELECT ? ,? ,? , t.id
                FROM todo t
                WHERE t.id = ?;
                """).withArgs(content,starttime,endtime,idtodo);

        return getMysqlClient().executeThenGet(query)
                .thenApply(MultiRows::getRows) //
                .thenApply(row -> row.property(LAST_INSERTED_ID));
    }

    @Override
    public CompletionStage<Void> updateWhattodo(int id, String content, LocalDateTime starttime, LocalDateTime endtime) {
        var query = SqlQuery.of("""
                UPDATE whattodo
                SET content = ?, starttime = ?, endtime = ?
                WHERE id = ?
                """).withArgs(content, starttime,endtime,id);

        return getMysqlClient().executeThenClose(query);
    }



    @Override
    public CompletionStage<Void> deleteWhattodo(int id) {
        var query = SqlQuery.of("""
                DELETE FROM whattodo
                WHERE id = ?
                """).withArg(id);
        return getMysqlClient().executeThenClose(query);
    }


    @Override
    public CompletionStage<List<Whattodo>> getWhattodo(int idtodo) {
        var query = SqlQuery.of("""
                SELECT idtodo, content, starttime, endtime
                FROM whattodo
                WHERE idtodo = ?
                """).withArg(idtodo);

        return getMysqlClient().executeThenGet(query)
                .thenApply(rows -> {
                    List<Whattodo> wtds = rows.mapTo(Whattodo.class);
                    if (wtds.isEmpty()) {
                        throw new NotFoundException("No whattodo found");
                    }
                    return wtds;
                });
    }

    @Override
    public CompletionStage<Void> transformWhattodo(int id, int idtodo) {
        var query = SqlQuery.of("""
                UPDATE whattodo
                SET idtodo = ?
                WHERE id = ?
                """).withArgs(id, idtodo);

        return getMysqlClient().executeThenClose(query);
    }


}
