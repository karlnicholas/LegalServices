package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.github.karlnicholas.legalservices.opinionview.dao.SlipOpininScraperDao;
import com.github.karlnicholas.legalservices.opinionview.kafka.KakfaProperties;
import com.github.karlnicholas.legalservices.opinionview.kafka.OpinionViewMessage;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.caselist.model.CASELISTSTATUS;
import com.github.karlnicholas.legalservices.caselist.model.CaseListEntries;
import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;

public class CaseListProcessorComponent implements Runnable {
	private final Logger log = LoggerFactory.getLogger(CaseListProcessorComponent.class);
	private final Consumer<Integer, JsonNode> consumer;
	private final Producer<Integer, JsonNode> producer;
	private final Producer<Integer, OpinionViewMessage> cacheProducer;
	private final SlipOpininScraperDao slipOpininScraperDao;
	private final ObjectMapper objectMapper;
	private final KakfaProperties kafkaProperties;
	private final DataSource dataSource;
	private static final int MAX_ENTRIES_PROCESS = 1000;

	public CaseListProcessorComponent(ObjectMapper objectMapper, 
			KakfaProperties kafkaProperties, 
			Producer<Integer, JsonNode> producer,
			Producer<Integer, OpinionViewMessage> cacheProducer, 
			DataSource dataSource
	) {
		this.objectMapper = objectMapper;
		this.kafkaProperties = kafkaProperties;
		this.dataSource = dataSource;
		this.producer = producer;
		this.cacheProducer = cacheProducer;
		slipOpininScraperDao = new SlipOpininScraperDao();
        //Configure the Consumer
		Properties consumerProperties = new Properties();
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getIntegerDeserializer());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getJsonValueDeserializer());
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getCaseListEntriesConsumerGroup());
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
		    consumer.subscribe(Collections.singletonList(kafkaProperties.getCaseListEntriesTopic()));
		    while (true) {
		    	try {
			        ConsumerRecords<Integer, JsonNode> records = consumer.poll(Duration.ofSeconds(1));
			        for (ConsumerRecord<Integer, JsonNode> record : records) {
//			        	log.info("topic = {}, partition = {}, offset = {}, record key = {}, record value length = {}",
//			                 record.topic(), record.partition(), record.offset(),
//			                 record.key(), record.value().toString().length());
			        	CaseListEntries caseListEntries = objectMapper.treeToValue( record.value(), CaseListEntries.class);
			        	
			        	processCaseListEntries(caseListEntries);
			        	log.info("partition = {}, offset = {}, record key = {}, caseListEntries.length = {}",
			        			record.partition(), record.offset(), record.key(), caseListEntries.size());
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
	private void processCaseListEntries(CaseListEntries caseListEntries) throws SQLException {
		try (Connection con = dataSource.getConnection()) {
			CaseListEntries currentCaseListEntries = slipOpininScraperDao.caseListEntries(con);
			CaseListEntries newCaseListEntries = new CaseListEntries(new ArrayList<>());
			CaseListEntries existingCaseListEntries = new CaseListEntries(new ArrayList<>());

			for ( CaseListEntry caseListEntry: caseListEntries ) {
				int index = currentCaseListEntries.indexOf(caseListEntry);
				if ( index >= 0 ) {
					existingCaseListEntries.add(currentCaseListEntries.get(index));
				} else {
					newCaseListEntries.add(caseListEntry);
				}
			}
			// currentCaseListEntries will have only deleted items
			newCaseListEntries.forEach(cle->cle.setStatus(CASELISTSTATUS.PENDING));
			List<CaseListEntry> failedCaseListEntries = existingCaseListEntries.stream().filter(cle->cle.getStatus() != CASELISTSTATUS.PROCESSED).collect(Collectors.toList());
			failedCaseListEntries.removeIf(cle->cle.getStatus() == CASELISTSTATUS.FAILED);
			failedCaseListEntries.forEach(cle->{
				cle.setStatus(CASELISTSTATUS.FAILED);
				currentCaseListEntries.get(currentCaseListEntries.indexOf(cle)).setStatus(CASELISTSTATUS.FAILED);
			});
			List<CaseListEntry> deletedCaseListEntries = new ArrayList<>(currentCaseListEntries);
			deletedCaseListEntries.removeAll(existingCaseListEntries);
			deletedCaseListEntries.forEach(cle->cle.setStatus(CASELISTSTATUS.DELETED));
			// construct database update
			List<CaseListEntry> max10newList = newCaseListEntries.subList(0, newCaseListEntries.size() > MAX_ENTRIES_PROCESS ? MAX_ENTRIES_PROCESS : newCaseListEntries.size());
			currentCaseListEntries.addAll(max10newList);
			slipOpininScraperDao.caseListEntryUpdates(con, currentCaseListEntries);
			// send new cases
			max10newList.forEach(cle->{
				JsonNode  jsonNode = objectMapper.valueToTree(cle);
				ProducerRecord<Integer, JsonNode> rec = new ProducerRecord<>(kafkaProperties.getNewCaseListTopic(), jsonNode);
				producer.send(rec);
				log.info("New Case: {}", cle);
			});

			// send delete cases
			deletedCaseListEntries.forEach(cle->{
				ProducerRecord<Integer, OpinionViewMessage> rec = new ProducerRecord<>(kafkaProperties.getOpinionViewCacheTopic(),
						OpinionViewMessage.builder().caseListEntry(cle).build());
				cacheProducer.send(rec);
				log.info("Deleted Case: {}", cle);
			});

			// send failed cases
			failedCaseListEntries.forEach(cle->{
				JsonNode  jsonNode = objectMapper.valueToTree(cle);
				ProducerRecord<Integer, JsonNode> rec = new ProducerRecord<>(kafkaProperties.getFailCaseListTopic(), jsonNode);
				producer.send(rec);
				log.warn("Failed Case: {}", cle);
			});
		}
	}

}
