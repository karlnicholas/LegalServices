package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpinionViewCacheComponent implements Runnable {

//	private volatile boolean someCondition = true;
	private final Consumer<String, OpinionView> consumer;
	private final KakfaProperties kafkaProperties;
	private final OpinionViewData opinionViewData;

	public OpinionViewCacheComponent(KakfaProperties kafkaProperties, OpinionViewData opinionViewData) {
		this.kafkaProperties = kafkaProperties;
		this.opinionViewData = opinionViewData;

        //Configure the Consumer
		Properties consumerProperties = new Properties();
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getByteArrayKeyDeserializer());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getOpinionViewValueDeserializer());
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
		consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		// Create the consumer using props.
		 consumer = new KafkaConsumer<>(consumerProperties);
	}

	@Override
    public void run(){
		try {
			// Subscribe to the topic.
		    consumer.subscribe(Collections.singletonList(kafkaProperties.getOpinionViewCacheTopic()));
		    while (true) {
		        ConsumerRecords<String, OpinionView> records = consumer.poll(Duration.ofMillis(100));
		        for (ConsumerRecord<String, OpinionView> record : records) {
		        	log.debug("topic = {}, partition = {}, offset = {}, record key = {}, record value length = {}",
		                 record.topic(), record.partition(), record.offset(),
		                 record.key(), record.value());
		        	OpinionView opinionView = record.value();
		        	opinionViewData.addOpinionView(opinionView);
		        }
		    }
		} catch (WakeupException e) {
		} catch (Exception e) {
//			if ( ! (e instanceof InterruptedException) )
				log.error("Unexpected error", e);
		} finally {
	        consumer.close();
		}
	}
}
