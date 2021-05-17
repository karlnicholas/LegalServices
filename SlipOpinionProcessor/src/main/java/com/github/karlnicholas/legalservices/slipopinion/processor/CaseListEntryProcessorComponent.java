package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.sql.SQLException;
import java.time.Duration;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.caselist.model.CASELISTSTATUS;
import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;
import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;
import com.github.karlnicholas.legalservices.opinion.parser.ScrapedOpinionDocument;
import com.github.karlnicholas.legalservices.opinion.service.OpinionService;
import com.github.karlnicholas.legalservices.opinion.service.OpinionServiceFactory;
import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;
import com.github.karlnicholas.legalservices.opinionview.model.OpinionViewBuilder;
import com.github.karlnicholas.legalservices.slipopinion.model.SlipOpinion;
import com.github.karlnicholas.legalservices.slipopinion.parser.OpinionScraperInterface;
import com.github.karlnicholas.legalservices.slipopinion.parser.SlipOpinionDocumentParser;
import com.github.karlnicholas.legalservices.slipopinion.scraper.TestCAParseSlipDetails;
import com.github.karlnicholas.legalservices.statute.service.StatuteService;
import com.github.karlnicholas.legalservices.statute.service.StatutesServiceFactory;


public class CaseListEntryProcessorComponent implements Runnable {
	private final Logger log = LoggerFactory.getLogger(CaseListEntryProcessorComponent.class);
	private final Consumer<Integer, JsonNode> newCaseListconsumer;
	private final Consumer<Integer, JsonNode> deleteCaseListConsumer;
	private final Producer<Integer, OpinionViewMessage> producer;
	private final ObjectMapper objectMapper;
	private final OpinionService opinionService;
	private final KakfaProperties kafkaProperties;
	private final OpinionScraperInterface caseScraper;
	private final SlipOpinionDocumentParser opinionDocumentParser;
	private final OpinionViewBuilder opinionViewBuilder;
	
	protected CaseListEntryProcessorComponent(ObjectMapper objectMapper, 
			KakfaProperties kafkaProperties,
			Producer<Integer, OpinionViewMessage> producer
	) {
		this.objectMapper = objectMapper;
		this.kafkaProperties = kafkaProperties; 
		this.producer = producer; 
	    opinionService = OpinionServiceFactory.getOpinionServiceClient(objectMapper);
//		caseScraper = new CACaseScraper(false);
		caseScraper = new TestCAParseSlipDetails(false);
	    StatuteService statutesService = StatutesServiceFactory.getStatutesServiceClient();
		opinionDocumentParser = new SlipOpinionDocumentParser(statutesService.getStatutesTitles().getBody());
		opinionViewBuilder = new OpinionViewBuilder(statutesService);

        //Configure the Consumer
		Properties consumerProperties = new Properties();
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getIntegerDeserializer());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getJsonValueDeserializer());
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getNewCaseListConsumerGroup());
        if ( !kafkaProperties.getUser().equalsIgnoreCase("notFound") ) {
        	consumerProperties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        	consumerProperties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        	consumerProperties.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" +
    		kafkaProperties.getUser() + "\" password=\"" + 
    		kafkaProperties.getPassword() + "\";");
        }

		// Create the consumer using props.
        newCaseListconsumer = new KafkaConsumer<>(consumerProperties);
        //Configure the Consumer
		consumerProperties = new Properties();
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getIntegerDeserializer());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getJsonValueDeserializer());
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getDeleteCaseListConsumerGroup());
        if ( !kafkaProperties.getUser().equalsIgnoreCase("notFound") ) {
        	consumerProperties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        	consumerProperties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        	consumerProperties.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" +
    		kafkaProperties.getUser() + "\" password=\"" + 
    		kafkaProperties.getPassword() + "\";");
        }

		// Create the consumer using props.
        deleteCaseListConsumer = new KafkaConsumer<>(consumerProperties);
	}

	@Override
    public void run(){
		try {
			// Subscribe to the topic.
			newCaseListconsumer.subscribe(Collections.singletonList(kafkaProperties.getNewCaseListTopic()));
			deleteCaseListConsumer.subscribe(Collections.singletonList(kafkaProperties.getDeleteCaseListTopic()));
		    while (true) {
		    	try {
			        ConsumerRecords<Integer, JsonNode> records = newCaseListconsumer.poll(Duration.ofSeconds(1));
			        for (ConsumerRecord<Integer, JsonNode> record : records) {
//			        	log.info("topic = {}, partition = {}, offset = {}, record key = {}, record value length = {}",
//			                 record.topic(), record.partition(), record.offset(),
//			                 record.key(), record.value().toString().length());
			        	CaseListEntry newCaseListEntry = objectMapper.treeToValue( record.value(), CaseListEntry.class);
			        	processSlipOpinion(newCaseListEntry);
			        	log.info("partition = {}, offset = {}, record key = {}, caseListEntries = {}",
			        			record.partition(), record.offset(), record.key(), newCaseListEntry);
			        }
			        ConsumerRecords<Integer, JsonNode> deleteRecords = deleteCaseListConsumer.poll(Duration.ofSeconds(1));
			        for (ConsumerRecord<Integer, JsonNode> deleteRecord : deleteRecords) {
//			        	log.debug("topic = {}, partition = {}, offset = {}, record key = {}, record value length = {}",
//			                 record.topic(), record.partition(), record.offset(),
//			                 record.key(), record.value());
			        	CaseListEntry deleteCaseListEntry = objectMapper.treeToValue( deleteRecord.value(), CaseListEntry.class);
			        	log.info("partition = {}, offset = {}, record key = {}, caseListEntries = {}",
			        			deleteRecord.partition(), deleteRecord.offset(), deleteRecord.key(), deleteCaseListEntry);
						ProducerRecord<Integer, OpinionViewMessage> rec = new ProducerRecord<>(kafkaProperties.getOpinionViewCacheTopic(), 
								OpinionViewMessage.builder().caseListEntry(deleteCaseListEntry).build());
					    producer.send(rec);
			        }
				} catch (Exception e) {
					log.error("Unexpected error: {}", e);
				}
		    }
		} catch (WakeupException e) {
			log.error("WakeupException: {}", e);
		} finally {
			newCaseListconsumer.close();
			deleteCaseListConsumer.close();
		}
	}
	public void processSlipOpinion(CaseListEntry caseListEntry) throws SQLException {
		try {
			SlipOpinion slipOpinion = new SlipOpinion(caseListEntry.getFileName(), caseListEntry.getFileExtension(), caseListEntry.getTitle(), caseListEntry.getOpinionDate(), caseListEntry.getCourt(), caseListEntry.getSearchUrl());
		    ScrapedOpinionDocument scrapedOpinionDocument = caseScraper.scrapeOpinionFile(slipOpinion);
		
			opinionDocumentParser.parseOpinionDocument(scrapedOpinionDocument, scrapedOpinionDocument.getOpinionBase());
			// maybe someday deal with court issued modifications
			opinionDocumentParser.parseSlipOpinionDetails((SlipOpinion) scrapedOpinionDocument.getOpinionBase(), scrapedOpinionDocument);
			List<OpinionKey> opinionKeys = slipOpinion.getOpinionCitations()
					.stream()
					.map(OpinionBase::getOpinionKey)
					.collect(Collectors.toList());
			
			List<OpinionBase> opinionsWithReferringOpinions = opinionService.getOpinionsWithStatuteCitations(opinionKeys).getBody();

			slipOpinion.getOpinionCitations().clear();
			slipOpinion.getOpinionCitations().addAll(opinionsWithReferringOpinions);

			OpinionView opinionView = opinionViewBuilder.buildOpinionView(slipOpinion);
		    	        	
			ProducerRecord<Integer, OpinionViewMessage> rec = new ProducerRecord<>(kafkaProperties.getOpinionViewCacheTopic(), 
					OpinionViewMessage.builder().opinionView(opinionView).build());
		    producer.send(rec);
			caseListEntry.setStatus(CASELISTSTATUS.PROCESSED);
		} catch ( Exception ex) {
			caseListEntry.setStatus(CASELISTSTATUS.ERROR);
			log.error("SlipOpinion error: {} {} {}", caseListEntry.getId(), caseListEntry.getFileName(), ex.toString());
		} finally {
			opinionService.caseListEntryUpdate(caseListEntry);
		}
	}
}
