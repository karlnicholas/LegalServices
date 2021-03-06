package com.github.karlnicholas.opinionservices.slipopinion.processor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@ConfigurationProperties(prefix = "com.github.karlnicholas.opinionservices.kafka")
@Data
public class KakfaProperties {
    
    private String ipAddress;
    private int port;
    private String slipOpinionsTopic;
    private String opinionViewsTopic;
    private String keySerializer;
    private String valueSerializer;
    private String keyDeserializer;
    private String valueDeserializer;
    private String slipOpinionsConsumerGroup;
    private String opinionViewsConsumerGroup;
    // standard getters and setters
}
