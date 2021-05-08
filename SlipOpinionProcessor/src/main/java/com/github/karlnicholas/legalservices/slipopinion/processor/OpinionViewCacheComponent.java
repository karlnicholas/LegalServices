package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.time.Duration;
import java.util.Collections;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;

import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpinionViewCacheComponent implements Runnable {

//	private volatile boolean someCondition = true;
	private final Consumer<Integer, OpinionView> consumer;
	private final KakfaProperties kafkaProperties;
	private final OpinionViewData opinionViewData;

	public OpinionViewCacheComponent(
			KakfaProperties kafkaProperties, 
			OpinionViewData opinionViewData, 
			Consumer<Integer, OpinionView> consumer
	) {
		this.kafkaProperties = kafkaProperties;
		this.opinionViewData = opinionViewData;
		this.consumer = consumer;
	}

	@Override
    public void run(){
		try {
			// Subscribe to the topic.
		    consumer.subscribe(Collections.singletonList(kafkaProperties.getOpinionViewCacheTopic()));
		    while (true) {
		        ConsumerRecords<Integer, OpinionView> records = consumer.poll(Duration.ofSeconds(1));
		        for (ConsumerRecord<Integer, OpinionView> record : records) {
//		        	log.debug("topic = {}, partition = {}, offset = {}, record key = {}, record value length = {}",
//		                 record.topic(), record.partition(), record.offset(),
//		                 record.key(), record.value());
		        	OpinionView opinionView = record.value();
		        	opinionViewData.addOpinionView(opinionView);
		        }
		    }
		} catch (WakeupException e) {
			log.error("WakeupException: {}", e);
		} catch (Exception e) {
			log.error("Unexpected error: {}", e);
		} finally {
	        consumer.close();
		}
	}
}
