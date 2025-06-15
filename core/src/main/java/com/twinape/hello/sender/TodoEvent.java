package com.twinape.hello.sender;

import com.twinape.mood.social.rsync.client.RsyncEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
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