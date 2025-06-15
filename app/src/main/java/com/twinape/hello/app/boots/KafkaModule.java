package com.twinape.hello.app.boots;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.twinape.hello.app.HelloTwinApeAppConfig;
import com.twinape.rsync.KafkaTodoRsyncProducer;
import com.twinape.rsync.KafkaTodoRsyncProducerConfig;
import com.twinape.rsync.TodoRsyncConsumer;
import io.vertx.core.Vertx;
import com.twinape.mood.social.rsync.client.RsyncEvent;

import io.vertx.kafka.client.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import com.twinape.mood.social.rsync.client.serdes.RsyncEventSerializer;

public final class KafkaModule extends AbstractModule {

    @Provides
    @Singleton
    KafkaProducer<String, RsyncEvent> kafkaProducer(Vertx vertx, HelloTwinApeAppConfig appconfig) {
        var kafkaConfig  = appconfig.getKafka();
        return KafkaProducer.create(vertx, kafkaConfig.toProperties(), new StringSerializer(), new RsyncEventSerializer());
    }

    @Override
    protected void configure() {
          bind(KafkaTodoRsyncProducer.class).in(Singleton.class);
          bind(TodoRsyncConsumer.class).asEagerSingleton();
    }
}
