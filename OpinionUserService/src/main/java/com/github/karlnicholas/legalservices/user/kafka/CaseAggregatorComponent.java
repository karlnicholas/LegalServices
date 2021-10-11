package com.github.karlnicholas.legalservices.user.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import com.github.karlnicholas.legalservices.opinionview.model.OpinionViewMessage;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CaseAggregatorComponent implements Runnable {
    private final Logger log = LoggerFactory.getLogger(CaseAggregatorComponent.class);
    private final Consumer<Integer, OpinionViewMessage> consumer;
    private final KakfaProperties kafkaProperties;

    public CaseAggregatorComponent(
            ObjectMapper objectMapper,
            KakfaProperties kafkaProperties
    ) {
        this.kafkaProperties = kafkaProperties;

        //Configure the Consumer
        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getIpAddress() + ':' + kafkaProperties.getPort());
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getIntegerDeserializer());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getOpinionViewMessageDeserializer());
//		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getOpinionViewCacheConsumerGroup());
//		consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        if (!kafkaProperties.getUser().equalsIgnoreCase("notFound")) {
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
    public void run() {
        try {
            // Subscribe to the topic.
            consumer.subscribe(Collections.singletonList(kafkaProperties.getCaseListEntriesTopic()));
            while (true) {
                ConsumerRecords<Integer, OpinionViewMessage> opinionViewMessageRecords = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<Integer, OpinionViewMessage> opinionViewMessageRecord : opinionViewMessageRecords) {
//		        	log.info("topic = {}, partition = {}, offset = {}, record key = {}, record value length = {}",
//		        			opinionViewMessageRecord.topic(), opinionViewMessageRecord.partition(), opinionViewMessageRecord.offset());
                    OpinionViewMessage opinionViewMessage = opinionViewMessageRecord.value();
                }
            }
        } catch (WakeupException e) {
            log.error("WakeupException: {}", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e);
        }
    }
}
