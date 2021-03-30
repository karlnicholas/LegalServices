package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;
import com.github.karlnicholas.legalservices.opinion.service.OpinionService;
import com.github.karlnicholas.legalservices.opinion.service.OpinionServiceFactory;
import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;
import com.github.karlnicholas.legalservices.opinionview.model.OpinionViewBuilder;
import com.github.karlnicholas.legalservices.slipopinion.model.SlipOpinion;
import com.github.karlnicholas.legalservices.statute.service.StatuteService;
import com.github.karlnicholas.legalservices.statute.service.StatutesServiceFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpinionViewBuildComponent implements Runnable {

	private final Consumer<Integer, JsonNode> consumer;
	private final Producer<Integer, OpinionView> producer;
	private final ObjectMapper objectMapper;
	private final StatuteService statutesService;
	private final OpinionService opinionService;
	private final OpinionViewBuilder opinionViewBuilder;
	private final KakfaProperties kafkaProperties;

	public OpinionViewBuildComponent(ObjectMapper objectMapper, KakfaProperties kafkaProperties) {
		this.objectMapper = objectMapper;
		this.kafkaProperties = kafkaProperties; 
	    statutesService = StatutesServiceFactory.getStatutesServiceClient();
	    opinionService = OpinionServiceFactory.getOpinionServiceClient();
		opinionViewBuilder = new OpinionViewBuilder(statutesService);
        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,kafkaProperties.getIntegerSerializer());
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,kafkaProperties.getOpinionViewValueSerializer());
        
        producer = new KafkaProducer<>(configProperties);

        //Configure the Consumer
		Properties consumerProperties = new Properties();
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getIntegerDeserializer());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getJsonValueDeserializer());

		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getSlipOpinionsConsumerGroup());

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
			        ConsumerRecords<Integer, JsonNode> records = consumer.poll(Duration.ofMillis(100));
			        for (ConsumerRecord<Integer, JsonNode> record : records) {
//			        	log.info("topic = {}, partition = {}, offset = {}, record key = {}, record value length = {}",
//			                 record.topic(), record.partition(), record.offset(),
//			                 record.key(), record.value().toString().length());
			        	SlipOpinion slipOpinion = objectMapper.treeToValue( record.value(), SlipOpinion.class);
			        	OpinionView opinionView = buildOpinionView(slipOpinion);
			        	producer.send(new ProducerRecord<Integer, OpinionView>(kafkaProperties.getOpinionViewCacheTopic(), slipOpinion.getOpinionKey().hashCode(), opinionView));
			        	log.info("partition = {}, offset = {}, record key = {}, opinionView = {}",
			        			record.partition(), record.offset(), record.key(), opinionView);
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
	private OpinionView buildOpinionView(SlipOpinion slipOpinion) {
		List<OpinionKey> opinionKeys = slipOpinion.getOpinionCitations()
				.stream()
				.map(OpinionBase::getOpinionKey)
				.collect(Collectors.toList());
		
		List<OpinionBase> opinionsWithReferringOpinions = opinionService.getOpinionsWithStatuteCitations(opinionKeys).getBody();

		slipOpinion.getOpinionCitations().clear();
		slipOpinion.getOpinionCitations().addAll(opinionsWithReferringOpinions);

		return opinionViewBuilder.buildOpinionView(slipOpinion);
	}

}
