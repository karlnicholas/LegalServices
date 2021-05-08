package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
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
public class CaseListProcessorComponent implements Runnable {

	private final Consumer<Integer, JsonNode> consumer;
	private final Producer<Integer, JsonNode> producer;
	private final ObjectMapper objectMapper;
	private final OpinionService opinionService;
	private final KakfaProperties kafkaProperties;

	public CaseListProcessorComponent(ObjectMapper objectMapper, 
			KakfaProperties kafkaProperties, 
			Producer<Integer, JsonNode> producer
	) {
		this.objectMapper = objectMapper;
		this.kafkaProperties = kafkaProperties;
		this.producer = producer;
	    opinionService = OpinionServiceFactory.getOpinionServiceClient();
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
//			        	log.info("topic = {}, partition = {}, offset = {}, record key = {}, record value length = {}",
//			                 record.topic(), record.partition(), record.offset(),
//			                 record.key(), record.value().toString().length());
			        	CaseListEntry[] caseListEntries = objectMapper.treeToValue( record.value(), CaseListEntry[].class);
			        	
			        	processCaseListEntries(caseListEntries);
			        	log.info("partition = {}, offset = {}, record key = {}, caseListEntries.length = {}",
			        			record.partition(), record.offset(), record.key(), caseListEntries.length);
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
	private void processCaseListEntries(CaseListEntry[] caseListEntries) {
		List<CaseListEntry> currentCaseListEntries = opinionService.caseListEntries().getBody();
		List<CaseListEntry> newCaseListEntries = new ArrayList<>();
		List<CaseListEntry> existingCaseListEntries = new ArrayList<>();

		for ( CaseListEntry caseListEntry: caseListEntries ) {
			if ( currentCaseListEntries.contains(caseListEntry) ) {
				existingCaseListEntries.add(currentCaseListEntries.get(currentCaseListEntries.indexOf(caseListEntry)));
			} else {
				newCaseListEntries.add(caseListEntry);
			}
		}
		// currentCaseListEntries will have only deleted items
		newCaseListEntries.forEach(cle->cle.setStatus(CASELISTSTATUS.PENDING));
		List<CaseListEntry> retryCaseListEntries = existingCaseListEntries.stream().filter(cle->cle.getStatus() != CASELISTSTATUS.PROCESSED).collect(Collectors.toList());
		List<CaseListEntry> failedCaseListEntries = retryCaseListEntries.stream().filter(cle->cle.getStatus() != CASELISTSTATUS.RETRY).collect(Collectors.toList());
		List<CaseListEntry> deletedCaseListEntries = new ArrayList<>(currentCaseListEntries);
		deletedCaseListEntries.removeAll(existingCaseListEntries);
		deletedCaseListEntries.forEach(cle->cle.setStatus(CASELISTSTATUS.DELETED));
		// construct database update
		currentCaseListEntries.addAll(newCaseListEntries);
		opinionService.caseListEntryUpdates(currentCaseListEntries);
		// send new cases
		newCaseListEntries.forEach(cle->{
		    JsonNode  jsonNode = objectMapper.valueToTree(cle);
		    ProducerRecord<Integer, JsonNode> rec = new ProducerRecord<>(kafkaProperties.getNewCaseListTopic(), jsonNode);
		    producer.send(rec);
		});

		// send retry cases
		retryCaseListEntries.forEach(cle->{
		    JsonNode  jsonNode = objectMapper.valueToTree(cle);
		    ProducerRecord<Integer, JsonNode> rec = new ProducerRecord<>(kafkaProperties.getRetryCaseListTopic(), jsonNode);
		    producer.send(rec);
		});
		
		// send delete cases
		deletedCaseListEntries.forEach(cle->{
		    JsonNode  jsonNode = objectMapper.valueToTree(cle);
		    ProducerRecord<Integer, JsonNode> rec = new ProducerRecord<>(kafkaProperties.getDeleteCaseListTopic(), jsonNode);
		    producer.send(rec);
		});
		
		// send delete cases
		failedCaseListEntries.forEach(cle->{
		    JsonNode  jsonNode = objectMapper.valueToTree(cle);
		    ProducerRecord<Integer, JsonNode> rec = new ProducerRecord<>(kafkaProperties.getFailCaseListTopic(), jsonNode);
		    producer.send(rec);
		});
	
	}

}
