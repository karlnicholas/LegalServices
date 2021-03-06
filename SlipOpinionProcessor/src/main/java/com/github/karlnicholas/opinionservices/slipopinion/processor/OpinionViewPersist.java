package com.github.karlnicholas.opinionservices.slipopinion.processor;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
import opca.view.OpinionView;

@Slf4j
public class OpinionViewPersist implements Runnable {

//	private volatile boolean someCondition = true;
	private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
	private Consumer<String, JsonNode> consumer;
	private final ObjectMapper objectMapper;
	private final KakfaProperties kafkaProperties;
	
	public OpinionViewPersist(ObjectMapper objectMapper, KakfaProperties kafkaProperties) {
		this.objectMapper = objectMapper;
		this.kafkaProperties = kafkaProperties;

        //Configure the Consumer
		Properties consumerProperties = new Properties();
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getKeyDeserializer());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getValueDeserializer());

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
		// Subscribe to the topic.
		try {
		    consumer.subscribe(Collections.singletonList(kafkaProperties.getOpinionViewsTopic()), new HandleRebalance());
		    while (true) {
		        ConsumerRecords<String, JsonNode> records = consumer.poll(Duration.ofMillis(100));
		        for (ConsumerRecord<String, JsonNode> record : records) {
		        	log.info("topic = {}, partition = {}, offset = {}, record key = {}, record value length = {}",
		                 record.topic(), record.partition(), record.offset(),
		                 record.key(), record.value().toString().length());
		        	OpinionView opinionView = objectMapper.treeToValue( record.value(), OpinionView.class);
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
}
