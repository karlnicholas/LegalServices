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

import com.github.karlnicholas.opinionservices.slipopinion.dao.OpinionViewDao;
import com.github.karlnicholas.opinionservices.slipopinion.dao.OpinionViewDeserializer;

import lombok.extern.slf4j.Slf4j;
import opca.view.OpinionView;

@Slf4j
public class OpinionViewCache implements Runnable {

//	private volatile boolean someCondition = true;
	private final Map<TopicPartition, OffsetAndMetadata> currentOffsets;
	private final Consumer<String, Integer> consumer;
	private final KakfaProperties kafkaProperties;
	private final OpinionViewDao opinionViewDao;
	private final OpinionViewDeserializer opinionViewDeserializer;

	public OpinionViewCache(KakfaProperties kafkaProperties, OpinionViewDao opinionViewDao) {
		this.kafkaProperties = kafkaProperties; 
		this.opinionViewDao = opinionViewDao;
		opinionViewDeserializer = new OpinionViewDeserializer();
		currentOffsets = new HashMap<>();

        //Configure the Consumer
		Properties consumerProperties = new Properties();
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getByteArrayKeyDeserializer());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getIntegerValueDeserializer());

		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getOpinionViewCacheConsumerGroup());

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
		    consumer.subscribe(Collections.singletonList(kafkaProperties.getOpinionViewCacheTopic()), new HandleRebalance());
		    while (true) {
		        ConsumerRecords<String, Integer> records = consumer.poll(Duration.ofMillis(100));
		        for (ConsumerRecord<String, Integer> record : records) {
		        	log.info("topic = {}, partition = {}, offset = {}, record key = {}, record value length = {}",
		                 record.topic(), record.partition(), record.offset(),
		                 record.key(), record.value());
		        	Integer id = record.value();
		        	byte[] opinionViewBytes = opinionViewDao.getOpinionViewBytesForId(id);
		        	OpinionView opinionView = opinionViewDeserializer.deserialize(opinionViewBytes);
System.out.println(opinionView);		        	
		        	// ... 
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
