package com.twinape.hello.repo.Whattodo;

import javax.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletionStage;

public interface WhattodoRepo {
    CompletionStage<Long> addWhattodo(String content, LocalDateTime starttime, LocalDateTime endtime, int idtodo);

    CompletionStage<Void> updateWhattodo(int id, String content, LocalDateTime starttime, LocalDateTime endtime);

    CompletionStage<Void> deleteWhattodo(int id);

    CompletionStage<List<Whattodo>> getWhattodo(int idtodo, int limit, int offset);

    CompletionStage<Void> transformWhattodo(int id, int idtodo);

    CompletionStage<List<WhattodoDetail>> detailWhattodo(int limit, int offset);

    CompletionStage<List<Whattodo>> gettimeWhattodo(LocalDateTime starttime, LocalDateTime endtime, @Nullable Integer idtodo, int limit, int offset);

}
