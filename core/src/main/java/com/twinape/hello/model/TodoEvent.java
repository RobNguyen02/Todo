package com.twinape.hello.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TodoEvent {
    String type;
    Integer id;
    String title;
    String descr;
    boolean isComplete;
}
