package com.github.karlnicholas.opinionservices.slipopinion.processor;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.SlipOpinion;
import opca.parser.OpinionScraperInterface;
import opca.parser.ScrapedOpinionDocument;
import opca.parser.SlipOpinionDocumentParser;
import opca.scraper.TestCAParseSlipDetails;
import opca.view.OpinionView;
import opca.view.OpinionViewBuilder;
import opinions.service.OpinionsService;
import opinions.service.client.OpinionsServiceClientImpl;
import statutes.StatutesTitles;
import statutes.service.StatutesService;
import statutes.service.client.StatutesServiceClientImpl;

@Slf4j
public class KafkaConsumerTask implements Runnable {

//	private volatile boolean someCondition = true;
	private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
	private Consumer<String, JsonNode> consumer;
	private final ObjectMapper objectMapper;
	
	public KafkaConsumerTask(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	private class HandleRebalance implements ConsumerRebalanceListener {
	    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
	    }

	    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
	    	log.warn("Lost partitions in rebalance. " + "Committing current offsets:" + currentOffsets);
	        consumer.commitSync(currentOffsets);
	    }
	}
	@Override
    public void run(){
		StatutesService statutesService = new StatutesServiceClientImpl("http://localhost:8090/");
		OpinionsService opinionsService = new OpinionsServiceClientImpl("http://localhost:8091/");
		OpinionViewBuilder opinionViewBuilder = new OpinionViewBuilder(statutesService);
		StatutesTitles[] arrayStatutesTitles = statutesService.getStatutesTitles().getBody();
		OpinionScraperInterface caseScraper = new TestCAParseSlipDetails(false);

		final Properties configProperties = new Properties();
		configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.202.101:32369");
		configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
		configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonDeserializer");

		configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaSlipOpinionConsumer");

		// Create the consumer using props.
		 consumer = new KafkaConsumer<>(configProperties);
		// Subscribe to the topic.
		try {
		    consumer.subscribe(Collections.singletonList("slipopinions"), new HandleRebalance());
		    while (true) {
		        ConsumerRecords<String, JsonNode> records = consumer.poll(Duration.ofMillis(100));
		        for (ConsumerRecord<String, JsonNode> record : records) {
		        	log.info("topic = {}, partition = {}, offset = {}, record key = {}, record value length = {}",
		                 record.topic(), record.partition(), record.offset(),
		                 record.key(), record.value().toString().length());
		        	SlipOpinion slipOpinion = objectMapper.treeToValue( record.value(), SlipOpinion.class);
		        	OpinionView opinionView = parseAndPrintOpinion(opinionsService, opinionViewBuilder, arrayStatutesTitles, caseScraper, slipOpinion);
		        	log.info("opinionView = {}", opinionView);
		            currentOffsets.put(
		                 new TopicPartition(record.topic(), record.partition()),
		                 new OffsetAndMetadata(record.offset()+1, null));
		        }
		        consumer.commitAsync(currentOffsets, null);
		    }
		} catch (WakeupException e) {
		} catch (Exception e) {
//			if ( ! (e instanceof InterruptedException) )
				log.error("Unexpected error", e);
		} finally {
		    try {
		        consumer.commitSync(currentOffsets);
		    } finally {
		        consumer.close();
		        System.out.println("Closed consumer and we are done");
		    }
		}
	}
	private OpinionView parseAndPrintOpinion(OpinionsService opinionsService, OpinionViewBuilder opinionViewBuilder,
			StatutesTitles[] arrayStatutesTitles, OpinionScraperInterface caseScraper, SlipOpinion slipOpinion) {
		// no retries
		ScrapedOpinionDocument scrapedOpinionDocument = caseScraper.scrapeOpinionFile(slipOpinion);

		SlipOpinionDocumentParser opinionDocumentParser = new SlipOpinionDocumentParser(arrayStatutesTitles);
		
		opinionDocumentParser.parseOpinionDocument(scrapedOpinionDocument, scrapedOpinionDocument.getOpinionBase());
		// maybe someday deal with court issued modifications
		opinionDocumentParser.parseSlipOpinionDetails((SlipOpinion) scrapedOpinionDocument.getOpinionBase(), scrapedOpinionDocument);
		
		List<OpinionKey> opinionKeys = slipOpinion.getOpinionCitations()
				.stream()
				.map(OpinionBase::getOpinionKey)
				.collect(Collectors.toList());
		
		List<OpinionBase> opinionsWithReferringOpinions = opinionsService.getOpinionsWithStatuteCitations(opinionKeys).getBody();

		slipOpinion.getOpinionCitations().clear();
		slipOpinion.getOpinionCitations().addAll(opinionsWithReferringOpinions);

		return opinionViewBuilder.buildOpinionView(slipOpinion);
	}

//	@Override
//	public void destroy() {
//		log.info("Stop Stuff");
//		someCondition = false;
//	}

}
