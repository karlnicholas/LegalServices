package com.github.karlnicholas.opinionservices.slipopinion.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "com.github.karlnicholas.opinionservices.kafka")
@Data
public class KakfaProperties {
    
    private String ipAddress;
    private int port;
    private String slipOpinionsTopic;
    private String opinionViewsTopic;
    private String opinionViewCacheTopic;
    private String byteArrayKeySerializer;
    private String jsonValueSerializer;
    private String integerValueSerializer;
    private String byteArrayKeyDeserializer;
    private String jsonValueDeserializer;
    private String integerValueDeserializer;
    private String slipOpinionsConsumerGroup;
    private String opinionViewsConsumerGroup;
    private String opinionViewCacheConsumerGroup;
    // standard getters and setters
}
