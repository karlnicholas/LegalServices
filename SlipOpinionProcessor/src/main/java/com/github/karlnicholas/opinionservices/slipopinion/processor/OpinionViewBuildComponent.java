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
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
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
public class OpinionViewBuildComponent implements Runnable {

//	private volatile boolean someCondition = true;
	private final Map<TopicPartition, OffsetAndMetadata> currentOffsets;
	private final Consumer<String, JsonNode> consumer;
	private final Producer<String, OpinionView> producer;
	private final ObjectMapper objectMapper;
	private final StatutesService statutesService;
	private final OpinionsService opinionsService;
	private final OpinionViewBuilder opinionViewBuilder;
	private final StatutesTitles[] arrayStatutesTitles;
	private final OpinionScraperInterface caseScraper;
	private final KakfaProperties kafkaProperties;

	public OpinionViewBuildComponent(ObjectMapper objectMapper, KakfaProperties kafkaProperties) {
		this.objectMapper = objectMapper;
		this.kafkaProperties = kafkaProperties; 
		currentOffsets = new HashMap<>();
		statutesService = new StatutesServiceClientImpl("http://localhost:8090/");
		opinionsService = new OpinionsServiceClientImpl("http://localhost:8091/");
		opinionViewBuilder = new OpinionViewBuilder(statutesService);
		arrayStatutesTitles = statutesService.getStatutesTitles().getBody();
		caseScraper = new TestCAParseSlipDetails(false);
        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,kafkaProperties.getByteArrayKeySerializer());
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,kafkaProperties.getOpinionViewValueSerializer());
        
        producer = new KafkaProducer<>(configProperties);

        //Configure the Consumer
		Properties consumerProperties = new Properties();
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getByteArrayKeyDeserializer());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getJsonValueDeserializer());

		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getSlipOpinionsConsumerGroup());

		// Create the consumer using props.
		 consumer = new KafkaConsumer<>(consumerProperties);
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
		
		try {
			// Subscribe to the topic.
		    consumer.subscribe(Collections.singletonList(kafkaProperties.getSlipOpinionsTopic()), new HandleRebalance());
		    while (true) {
		        ConsumerRecords<String, JsonNode> records = consumer.poll(Duration.ofMillis(100));
		        for (ConsumerRecord<String, JsonNode> record : records) {
		        	log.info("topic = {}, partition = {}, offset = {}, record key = {}, record value length = {}",
		                 record.topic(), record.partition(), record.offset(),
		                 record.key(), record.value().toString().length());
		        	SlipOpinion slipOpinion = objectMapper.treeToValue( record.value(), SlipOpinion.class);
		        	OpinionView opinionView = buildOpinionView(slipOpinion);
		        	producer.send(new ProducerRecord<String, OpinionView>(kafkaProperties.getOpinionViewCacheTopic(), opinionView));
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
	private OpinionView buildOpinionView(SlipOpinion slipOpinion) {
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
