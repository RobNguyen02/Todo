package com.twinape.rsync;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "_type")
public interface RsyncEvent {
    @JsonIgnore
    default Map<String, String> headers() {
        return Map.of();
    }

    @JsonIgnore
    default String key() {
        return null;
    }
}