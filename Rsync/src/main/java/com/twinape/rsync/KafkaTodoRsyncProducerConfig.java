package com.twinape.rsync;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Getter
@Builder
@ToString
@Jacksonized
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaTodoRsyncProducerConfig {

    private final String bootstrapServers;

    public Properties toProperties() {
        var props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return props;
    }
}
