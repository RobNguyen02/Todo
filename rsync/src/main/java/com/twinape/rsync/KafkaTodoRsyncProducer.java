package com.twinape.rsync;

import com.google.inject.Inject;
import com.twinape.hello.sender.RsyncEventSenderPort;
import com.twinape.mood.social.rsync.client.RsyncProducer;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import lombok.NonNull;
import com.twinape.mood.social.rsync.client.RsyncEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class KafkaTodoRsyncProducer implements RsyncEventSenderPort {
    private  final String TOPIC = "twa-todo-demo";

    private final @NonNull KafkaProducer<String, RsyncEvent> producer;

    @Inject
    public KafkaTodoRsyncProducer(@NonNull KafkaProducer<String, RsyncEvent> producer) {
        this.producer = producer;
    }
    public CompletionStage<Void> publish(RsyncEvent event) {
        try{
            var rec = KafkaProducerRecord.create(TOPIC, event.key(), event);
            var headers = event.headers();
            if (headers != null) {
                headers.forEach(rec::addHeader);
            }
            return producer.send(rec)
                    .<Void>mapEmpty()
                    .toCompletionStage();
        } catch (Throwable t) {
            return  CompletableFuture.failedStage(t);
        }
    }
}
