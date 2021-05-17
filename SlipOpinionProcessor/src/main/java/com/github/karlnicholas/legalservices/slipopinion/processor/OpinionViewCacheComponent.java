package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;
import com.github.karlnicholas.legalservices.slipopinion.model.SlipOpinion;

public class OpinionViewCacheComponent implements Runnable {
	private final Logger log = LoggerFactory.getLogger(OpinionViewCacheComponent.class);
	private final Consumer<Integer, OpinionViewMessage> opinionViewCacheConsumer;
	private final KakfaProperties kafkaProperties;
	private final OpinionViewData opinionViewData;

	public OpinionViewCacheComponent(
			ObjectMapper objectMapper, 
			KakfaProperties kafkaProperties, 
			OpinionViewData opinionViewData
	) {
		this.kafkaProperties = kafkaProperties;
		this.opinionViewData = opinionViewData;

		//Configure the Consumer
		Properties consumerProperties = new Properties();
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getIntegerDeserializer());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getOpinionViewMessageDeserializer());
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getOpinionViewCacheConsumerGroup());
        if ( !kafkaProperties.getUser().equalsIgnoreCase("notFound") ) {
        	consumerProperties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        	consumerProperties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        	consumerProperties.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" +
    		kafkaProperties.getUser() + "\" password=\"" + 
    		kafkaProperties.getPassword() + "\";");
        }

		// Create the consumer using props.
        opinionViewCacheConsumer = new KafkaConsumer<>(consumerProperties);

	}

	@Override
    public void run(){
		try {
			// Subscribe to the topics.
			opinionViewCacheConsumer.subscribe(Collections.singletonList(kafkaProperties.getOpinionViewCacheTopic()));
		    while (true) {
		        ConsumerRecords<Integer, OpinionViewMessage> opinionViewMessageRecords = opinionViewCacheConsumer.poll(Duration.ofSeconds(1));
		        for (ConsumerRecord<Integer, OpinionViewMessage> opinionViewMessageRecord : opinionViewMessageRecords) {
//		        	log.debug("topic = {}, partition = {}, offset = {}, record key = {}, record value length = {}",
//		                 record.topic(), record.partition(), record.offset(),
//		                 record.key(), record.value());
	        		OpinionViewMessage opinionViewMessage = opinionViewMessageRecord.value();
	        		if ( opinionViewMessage.getOpinionView().isPresent() ) {
			        	opinionViewData.addOpinionView(opinionViewMessage.getOpinionView().get());
	        		}
	        		if ( opinionViewMessage.getCaseListEntry().isPresent() ) {
	        			CaseListEntry caseListEntry = opinionViewMessage.getCaseListEntry().get();
	        			SlipOpinion slipOpinion = new SlipOpinion(caseListEntry.getFileName(), caseListEntry.getFileExtension(), caseListEntry.getTitle(), caseListEntry.getOpinionDate(), caseListEntry.getCourt(), caseListEntry.getSearchUrl());
			        	opinionViewData.deleteOpinionView(slipOpinion.getOpinionKey());
	        		}
		        }
		    }
		} catch (WakeupException e) {
			log.error("WakeupException: {}", e);
		} catch (Exception e) {
			log.error("Unexpected error: {}", e);
		} finally {
			opinionViewCacheConsumer.close();
		}
	}
}
