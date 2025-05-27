package com.twinape.hello.mysql.Whattodo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twinape.common.exception.NotFoundException;
import com.twinape.common.sql.SqlQuery;
import com.twinape.common.sql.extern.MultiRows;
import com.twinape.hello.mysql.AbstractMysqlRepo;
import com.twinape.hello.repo.Whattodo.Whattodo;
import com.twinape.hello.repo.Whattodo.WhattodoRepo;
import com.twinape.hello.repo.Whattodo.WhattodoDetail;
import lombok.AllArgsConstructor;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
                SELECT id, idtodo, content, starttime, endtime
                FROM whattodo
                WHERE idtodo = ?
                ORDER BY starttime ASC
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

    @Override
    public CompletionStage<List<WhattodoDetail>> detailWhattodo() {
        var query = SqlQuery.of("""
                SELECT
                w.id,
                w.content,
                w.starttime,
                w.endtime,
                t.title AS t_title,
                t.is_complete AS t_is_complete
                FROM
                whattodo w
                JOIN
                todo t on w.idtodo = t.id
                ORDER BY w.starttime DESC
                """);
        return getMysqlClient().executeThenGet(query)
                .thenApply(rows -> rows.mapTo(WhattodoDetail.class));
    }

    @Override
    public CompletionStage<List<Whattodo>> gettimeWhattodo(LocalDateTime starttime, LocalDateTime endtime, Integer idtodo) {
        StringBuilder sql = new StringBuilder("""
        SELECT id, content, starttime, endtime, idtodo
        FROM whattodo
        WHERE starttime >= ? AND endtime <= ?
    """);

        List<Object> args = new ArrayList<>();
        args.add(starttime);
        args.add(endtime);

        // Thêm điều kiện idtodo nếu có
        if (idtodo != null) {
            sql.append(" AND idtodo = ?");
            args.add(idtodo);
        }

        sql.append(" ORDER BY starttime ASC LIMIT 10 OFFSET 0");

        var query = SqlQuery.of(sql.toString()).withArgs(args.toArray());

        return getMysqlClient().executeThenGet(query)
                .thenApply(rows -> rows.mapTo(Whattodo.class));
    }

}
