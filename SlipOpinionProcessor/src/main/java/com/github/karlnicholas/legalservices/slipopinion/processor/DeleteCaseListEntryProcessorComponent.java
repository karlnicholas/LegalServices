package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.errors.WakeupException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.caselist.model.CASELISTSTATUS;
import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;
import com.github.karlnicholas.legalservices.opinion.service.OpinionService;
import com.github.karlnicholas.legalservices.opinion.service.OpinionServiceFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeleteCaseListEntryProcessorComponent implements Runnable {

	private final Consumer<Integer, JsonNode> consumer;
	private final Producer<Integer, JsonNode> producer;
	private final ObjectMapper objectMapper;
	private final OpinionService opinionService;
	private final KakfaProperties kafkaProperties;


	public DeleteCaseListEntryProcessorComponent(ObjectMapper objectMapper, KakfaProperties kafkaProperties) {
		this.objectMapper = objectMapper;
		this.kafkaProperties = kafkaProperties; 
	    opinionService = OpinionServiceFactory.getOpinionServiceClient();
        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,kafkaProperties.getIntegerSerializer());
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,kafkaProperties.getJsonValueSerializer());
        if ( !kafkaProperties.getUser().equalsIgnoreCase("notFound") ) {
            configProperties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
            configProperties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            configProperties.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" +
    		kafkaProperties.getUser() + "\" password=\"" + 
    		kafkaProperties.getPassword() + "\";");
        }
        
        producer = new KafkaProducer<>(configProperties);

        //Configure the Consumer
		Properties consumerProperties = new Properties();
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getIntegerDeserializer());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getJsonValueDeserializer());
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getSlipOpinionsConsumerGroup());
        if ( !kafkaProperties.getUser().equalsIgnoreCase("notFound") ) {
        	consumerProperties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        	consumerProperties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        	consumerProperties.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" +
    		kafkaProperties.getUser() + "\" password=\"" + 
    		kafkaProperties.getPassword() + "\";");
        }

		// Create the consumer using props.
		 consumer = new KafkaConsumer<>(consumerProperties);
	}

	@Override
    public void run(){
		try {
			// Subscribe to the topic.
		    consumer.subscribe(Collections.singletonList(kafkaProperties.getSlipOpinionsTopic()));
		    while (true) {
		    	try {
			        ConsumerRecords<Integer, JsonNode> records = consumer.poll(Duration.ofSeconds(1));
			        for (ConsumerRecord<Integer, JsonNode> record : records) {
			        	CaseListEntry caseListEntry = objectMapper.treeToValue( record.value(), CaseListEntry.class);
			        	processSlipOpinion(caseListEntry);
			        	log.info("partition = {}, offset = {}, record key = {}, caseListEntries = {}",
			        			record.partition(), record.offset(), record.key(), caseListEntry);
			        }
				} catch (Exception e) {
					log.error("Unexpected error: {}", e);
				}
		    }
		} catch (WakeupException e) {
			log.error("WakeupException: {}", e);
		} finally {
	        consumer.close();
		}
	}
	public void processSlipOpinion(CaseListEntry caseListEntry) throws SQLException {
		try {
		    JsonNode  jsonNode = objectMapper.valueToTree(caseListEntry);
		    ProducerRecord<Integer, JsonNode> rec = new ProducerRecord<>(kafkaProperties.getOpinionViewDeleteTopic(), jsonNode);
		    producer.send(rec);
			caseListEntry.setStatus(CASELISTSTATUS.DELETED);
		} catch ( Exception ex) {
			caseListEntry.setStatus(CASELISTSTATUS.ERROR);
			caseListEntry.setRetryCount(caseListEntry.getRetryCount()+1);
			log.error("SlipOpinion error: {}", caseListEntry);
		} finally {
			opinionService.caseListEntryUpdate(caseListEntry);
		}
	}
}
