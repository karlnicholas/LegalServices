package com.github.karlnicholas.opinionservices.slipopinion.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "com.github.karlnicholas.opinionservices.kafka")
@Data
public class KakfaProperties {
    
    private String ipAddress;
    private int port;
    private String slipOpinionsTopic;
    private String opinionViewCacheTopic;
    private String byteArrayKeySerializer;
    private String jsonValueSerializer;
    private String opinionViewValueSerializer;
    private String byteArrayKeyDeserializer;
    private String jsonValueDeserializer;
    private String opinionViewValueDeserializer;
    private String slipOpinionsConsumerGroup;
    private String opinionViewCacheConsumerGroup;
    // standard getters and setters
}
