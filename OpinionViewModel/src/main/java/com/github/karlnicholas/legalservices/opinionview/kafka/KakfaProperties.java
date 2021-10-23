package com.github.karlnicholas.legalservices.opinionview.kafka;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class KakfaProperties {
	@Value("${kafka.ip-address:localhost}")
	private String ipAddress;
	@Value("${kafka.port:9092}")
    private String port;
	@Value("${kafka.user:notFound}")
    private String user;
	@Value("${kafka.password:notFound}")
    private String password;
    private String caseListEntriesTopic = "caselistentries";
    private String caseListEntriesConsumerGroup = "caseListEntriesConsumerGroup";
    private String newCaseListTopic = "newcaselist";
    private String newCaseListConsumerGroup = "newCaseListConsumerGroup";
    private String failCaseListTopic = "failcaselist";
    private String failCaseListConsumerGroup = "failCaseListConsumerGroup";
    private String opinionViewCacheTopic = "opinionviewcache";
    private String opinionViewCacheConsumerGroup = "opinionViewCacheConsumerGroup";
    private String integerSerializer = "org.apache.kafka.common.serialization.IntegerSerializer";
    private String integerDeserializer = "org.apache.kafka.common.serialization.IntegerDeserializer";
    private String jsonValueSerializer = "org.apache.kafka.connect.json.JsonSerializer";
    private String jsonValueDeserializer = "org.apache.kafka.connect.json.JsonDeserializer";
    private String opinionViewMessageSerializer = "com.github.karlnicholas.legalservices.opinionview.kafka.OpinionViewMessageSerializer";
    private String opinionViewMessageDeserializer = "com.github.karlnicholas.legalservices.opinionview.kafka.OpinionViewMessageDeserializer";
}
