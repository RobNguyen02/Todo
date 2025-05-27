package com.twinape.hello.repo.Whattodo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;


@Getter
@Builder
@ToString
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class WhattodoDetail {
    int id;

    String content;

    LocalDateTime starttime;

    LocalDateTime endtime;

    String t_title;

    Boolean t_is_complete;

}
