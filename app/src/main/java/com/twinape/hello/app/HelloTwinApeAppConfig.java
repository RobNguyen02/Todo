package com.twinape.hello.app;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.twinape.common.bootstrap.CommonAppConfig;
import com.twinape.common.bootstrap.HazelcastConfig;
import com.twinape.common.bootstrap.MetricsConfig;
import com.twinape.common.bootstrap.VertxConfig;
import com.twinape.common.uri.ParsedUri;
import com.twinape.facade.http.config.HttpServerConfig;
import io.vertx.core.json.jackson.DatabindCodec;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;



@Getter
@Builder
@ToString
@Jacksonized
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public final class HelloTwinApeAppConfig implements CommonAppConfig {

    private final String appName;
    private final HazelcastConfig hazelcast;
    private final VertxConfig vertx;
    private final MetricsConfig metrics;
    private final MySqlConfig mySql;
    private final HttpServerConfig publicHttp;


    @Provides
    @Singleton
    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Đồng bộ với DatabindCodec
        DatabindCodec.mapper().registerModule(new JavaTimeModule());
        DatabindCodec.mapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
    //
    @Getter
    @Builder
    @ToString
    @Jacksonized
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class MySqlConfig {
        private final ParsedUri uri;
    }
}
