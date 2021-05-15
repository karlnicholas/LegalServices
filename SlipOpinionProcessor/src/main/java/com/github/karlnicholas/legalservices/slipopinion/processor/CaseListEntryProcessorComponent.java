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
import com.github.karlnicholas.legalservices.slipopinion.scraper.CACaseScraper;
import com.github.karlnicholas.legalservices.slipopinion.scraper.TestCAParseSlipDetails;
import com.github.karlnicholas.legalservices.statute.service.StatuteService;
import com.github.karlnicholas.legalservices.statute.service.StatutesServiceFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CaseListEntryProcessorComponent implements Runnable {
	private final Consumer<Integer, JsonNode> consumer;
	private final Producer<Integer, OpinionView> producer;
	private final ObjectMapper objectMapper;
	private final OpinionService opinionService;
	private final KakfaProperties kafkaProperties;
	private final OpinionScraperInterface caseScraper;
	private final SlipOpinionDocumentParser opinionDocumentParser;
	private final OpinionViewBuilder opinionViewBuilder;
	
	protected CaseListEntryProcessorComponent(ObjectMapper objectMapper, 
			KakfaProperties kafkaProperties,
			Producer<Integer, OpinionView> producer
	) {
		this.objectMapper = objectMapper;
		this.kafkaProperties = kafkaProperties; 
		this.producer = producer; 
	    opinionService = OpinionServiceFactory.getOpinionServiceClient();
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
		    consumer.subscribe(Collections.singletonList(kafkaProperties.getCaseListEntriesTopic()));
		    while (true) {
		    	try {
			        ConsumerRecords<Integer, JsonNode> records = consumer.poll(Duration.ofSeconds(1));
			        for (ConsumerRecord<Integer, JsonNode> record : records) {
//			        	log.info("topic = {}, partition = {}, offset = {}, record key = {}, record value length = {}",
//			                 record.topic(), record.partition(), record.offset(),
//			                 record.key(), record.value().toString().length());
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
		    	        	
			ProducerRecord<Integer, OpinionView> rec = new ProducerRecord<>(kafkaProperties.getOpinionViewCacheTopic(),slipOpinion.getOpinionKey().hashCode(), opinionView);
		    producer.send(rec);
			caseListEntry.setStatus(CASELISTSTATUS.PROCESSED);
		} catch ( Exception ex) {
			caseListEntry.setStatus(CASELISTSTATUS.FAILED);
			log.error("SlipOpinion error: {}", caseListEntry);
		} finally {
			opinionService.caseListEntryUpdate(caseListEntry);
		}
	}
}
