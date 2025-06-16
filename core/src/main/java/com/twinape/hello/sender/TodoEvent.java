package com.twinape.hello.sender;

import com.twinape.common.json.dynamic.JsonDynamicSubType;
import com.twinape.mood.social.rsync.client.RsyncEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@Builder
@Jacksonized
@AllArgsConstructor
@JsonDynamicSubType("todo")
public class TodoEvent implements RsyncEvent {
    private final String id;
    @Getter
    private final String content;

    @Override
    public String key() {
        return id;
    }

    @Override
    public Map<String, String> headers() {
        return Map.of("source", "todo-api");
    }
}