package com.github.karlnicholas.legalservices.slipopinion.processor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class KakfaProperties {
	@Value("${kafka.ip-address:localhost}")
	private String ipAddress;
	@Value("${kafka.port:9092}")
    private int port;
    private String slipOpinionsTopic = "slipopinions";
    private String opinionViewCacheTopic = "opinionviewcache";
    private String byteArrayKeySerializer = "org.apache.kafka.common.serialization.ByteArraySerializer";
    private String jsonValueSerializer = "org.apache.kafka.connect.json.JsonSerializer";
    private String opinionViewValueSerializer = "com.github.karlnicholas.legalservices.slipopinion.processor.OpinionViewSerializer";
    private String byteArrayKeyDeserializer = "org.apache.kafka.common.serialization.ByteArrayDeserializer";
    private String jsonValueDeserializer = "org.apache.kafka.connect.json.JsonDeserializer";
    private String opinionViewValueDeserializer = "com.github.karlnicholas.legalservices.slipopinion.processor.OpinionViewDeserializer";
    private String slipOpinionsConsumerGroup = "slipOpinionsConsumerGroup";
    private String opinionViewCacheConsumerGroup = "opinionViewCacheConsumerGroup";
    // standard getters and setters
}
