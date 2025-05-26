package com.twinape.hello.repo.Whattodo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletionStage;

public interface WhattodoRepo {
    CompletionStage<Long> addWhattodo(String content, LocalDateTime starttime, LocalDateTime endtime, int idtodo);

    CompletionStage<Void> updateWhattodo(int id, String content, LocalDateTime starttime, LocalDateTime endtime);

    CompletionStage<Void> deleteWhattodo(int id);

    CompletionStage<List<Whattodo>> getWhattodo(int idtodo);

    CompletionStage<Void> transformWhattodo(int id, int idtodo);
}
