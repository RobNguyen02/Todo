package com.twinape.rsync;
import com.twinape.mood.social.rsync.client.RsyncEvent;

import io.vertx.core.Vertx;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Singleton
public class TodoRsyncConsumer {

    @Inject
    public TodoRsyncConsumer(Vertx vertx) {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "com.twinape.mood.social.rsync.client.serdes.RsyncEventDeserializer");
        config.put("group.id", "todo-consumer");
        config.put("auto.offset.reset", "earliest");

        KafkaConsumer<String, RsyncEvent> consumer = KafkaConsumer.create(vertx, config);

        consumer.subscribe("twa-todo-demo");
        consumer.handler(record -> {
            RsyncEvent event = record.value();
            log.info("Received RsyncEvent on topic {}: {}", record.topic(), event);
        });

        consumer.exceptionHandler(err -> {
            log.error("Kafka consumer error", err);
        });
    }
}
