package com.twinape.pgsql.Whattodo;

import com.twinape.common.exception.NotFoundException;
import com.twinape.common.sql.SqlQuery;
import com.twinape.common.sql.extern.SingleRow;
import com.twinape.hello.repo.Whattodo.Whattodo;
import com.twinape.hello.repo.Whattodo.WhattodoDetail;
import com.twinape.hello.repo.Whattodo.WhattodoRepo;
import com.twinape.pgsql.AbstractPgsqlRepo;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class PgsqlWhattodoRepo extends AbstractPgsqlRepo implements WhattodoRepo {

    @Override
    public CompletionStage<Long> addWhattodo(String content, LocalDateTime starttime, LocalDateTime endtime, int idtodo) {
        var query = SqlQuery.of("""
                INSERT INTO whattodo (content, starttime, endtime, idtodo)
                SELECT $1, $2, $3, t.id
                FROM todo t
                WHERE t.id = $4
                RETURNING id
                """).withArgs(content, starttime, endtime, idtodo);
        return getPgsqlClient().executeThenGetFirst(query)
                .thenApply(SingleRow::getIdLong);
    }

    @Override
    public CompletionStage<Void> updateWhattodo(int id, String content, LocalDateTime starttime, LocalDateTime endtime) {
        var query = SqlQuery.of("""
                UPDATE whattodo
                SET content = $1, starttime = $2, endtime = $3
                WHERE id = $4
                """).withArgs(content, starttime, endtime, id);

        return getPgsqlClient().executeThenClose(query);
    }

    @Override
    public CompletionStage<Void> deleteWhattodo(int id) {
        var query = SqlQuery.of("""
                DELETE FROM whattodo
                WHERE id = $1
                """).withArg(id);
        return getPgsqlClient().executeThenClose(query);
    }

    @Override
    public CompletionStage<List<Whattodo>> getWhattodo(int idtodo, int limit, int offset) {
        var query = SqlQuery.of("""
                SELECT id, idtodo, content, starttime, endtime
                FROM whattodo
                WHERE idtodo = $1
                ORDER BY starttime ASC
                LIMIT $2 OFFSET $3
                """).withArgs(idtodo, limit, offset);

        return getPgsqlClient().executeThenGet(query)
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
                SET idtodo = $1
                WHERE id = $2
                """).withArgs(idtodo, id);

        return getPgsqlClient().executeThenClose(query);
    }

    @Override
    public CompletionStage<List<WhattodoDetail>> detailWhattodo(int limit, int offset) {
        var query = SqlQuery.of("""
                SELECT
                    w.id,
                    w.content,
                    w.starttime,
                    w.endtime,
                    t.title AS t_title,
                    t.is_complete AS t_is_complete
                FROM whattodo w
                JOIN todo t ON w.idtodo = t.id
                ORDER BY w.starttime DESC
                LIMIT $1 OFFSET $2
                """).withArgs(limit, offset);

        return getPgsqlClient().executeThenGet(query)
                .thenApply(rows -> rows.mapTo(WhattodoDetail.class));
    }

    @Override
    public CompletionStage<List<Whattodo>> gettimeWhattodo(LocalDateTime starttime,
                                                           LocalDateTime endtime,
                                                           @Nullable Integer idtodo,
                                                           int limit, int offset) {

        var query = new StringBuilder("""
                SELECT id, content, starttime, endtime, idtodo
                FROM whattodo
                WHERE starttime >= $1 AND endtime <= $2
                """);

        List<Object> args = new ArrayList<>();
        args.add(starttime);
        args.add(endtime);

        if (idtodo != null) {
            query.append(" AND idtodo = $3");
            args.add(idtodo);
            query.append(" ORDER BY starttime ASC LIMIT $4 OFFSET $5");
        } else {
            query.append(" ORDER BY starttime ASC LIMIT $3 OFFSET $4");
        }

        args.add(limit);
        args.add(offset);

        var querysql = SqlQuery.of(query.toString()).withArgs(args.toArray());

        return getPgsqlClient().executeThenGet(querysql)
                .thenApply(rows -> rows.mapTo(Whattodo.class));
    }
}
