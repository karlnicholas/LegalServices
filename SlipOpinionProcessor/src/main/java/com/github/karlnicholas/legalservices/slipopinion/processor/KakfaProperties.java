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
    private String port;
	@Value("${kafka.user:notFound}")
    private String user;
	@Value("${kafka.password:notFound}")
    private String password;
    private String caseListEntriesTopic = "caselistentries";
    private String newCaseListTopic = "newcaselist";
    private String retryCaseListTopic = "retrycaselist";
    private String deleteCaseListTopic = "deletecaselist";
    private String failCaseListTopic = "failcaselist";
    private String slipOpinionsTopic = "slipopinions";
    private String opinionViewCacheTopic = "opinionviewcache";
    private String opinionViewDeleteTopic = "opinionviewdelete";
    private String integerSerializer = "org.apache.kafka.common.serialization.IntegerSerializer";
    private String integerDeserializer = "org.apache.kafka.common.serialization.IntegerDeserializer";
    private String jsonValueSerializer = "org.apache.kafka.connect.json.JsonSerializer";
    private String jsonValueDeserializer = "org.apache.kafka.connect.json.JsonDeserializer";
    private String opinionViewSerializer = "com.github.karlnicholas.legalservices.slipopinion.processor.OpinionViewSerializer";
    private String opinionViewDeserializer = "com.github.karlnicholas.legalservices.slipopinion.processor.OpinionViewDeserializer";
    private String caseListEntrySerializer = "com.github.karlnicholas.legalservices.slipopinion.processor.CaseListEntrySerializer";
    private String caseListEntryDeserializer = "com.github.karlnicholas.legalservices.slipopinion.processor.CaseListEntryDeserializer";
    private String slipOpinionsConsumerGroup = "slipOpinionsConsumerGroup";
    private String opinionViewCacheConsumerGroup = "opinionViewCacheConsumerGroup";
}
