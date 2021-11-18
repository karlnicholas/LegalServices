package com.github.karlnicholas.legalservices.user.kafka;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;
import com.github.karlnicholas.legalservices.opinionview.dao.SlipOpininScraperDao;
import com.github.karlnicholas.legalservices.opinionview.kafka.KakfaProperties;
import com.github.karlnicholas.legalservices.opinionview.kafka.OpinionViewData;
import com.github.karlnicholas.legalservices.opinionview.kafka.OpinionViewMessage;
import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;
import com.github.karlnicholas.legalservices.slipopinion.model.SlipOpinion;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class EmailOpinionsComponent implements Runnable {
    private final Logger log = LoggerFactory.getLogger(EmailOpinionsComponent.class);
    private final DataSource dataSource;
    private final SlipOpininScraperDao slipOpininScraperDao;
    private final Consumer<Integer, OpinionViewMessage> opinionViewCacheConsumer;
    private final KakfaProperties kafkaProperties;
    private final OpinionViewData opinionViewData;

    public EmailOpinionsComponent(
            KakfaProperties kafkaProperties,
            OpinionViewData opinionViewData,
            ObjectMapper objectMapper,
            DataSource dataSource
    ) {
        this.dataSource = dataSource;
        slipOpininScraperDao = new SlipOpininScraperDao();

        this.kafkaProperties = kafkaProperties;
        this.opinionViewData = opinionViewData;

        //Configure the Consumer
        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaProperties.getIpAddress()+':'+kafkaProperties.getPort());
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getIntegerDeserializer());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getOpinionViewMessageDeserializer());
//		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getOpinionViewCacheConsumerGroup());
//		consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        if ( !kafkaProperties.getUser().equalsIgnoreCase("notFound") ) {
            consumerProperties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
            consumerProperties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            consumerProperties.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" +
                    kafkaProperties.getUser() + "\" password=\"" +
                    kafkaProperties.getPassword() + "\";");
        }

        // Create the consumer using props.
        opinionViewCacheConsumer = new KafkaConsumer<>(consumerProperties);

    }

    @Override
    public void run(){
        try {
            // Subscribe to the topics.
//			opinionViewCacheConsumer.subscribe(Collections.singletonList(kafkaProperties.getOpinionViewCacheTopic()));
//			opinionViewCacheConsumer.poll(Duration.ZERO);  // without this, the assignment will be empty.
//			opinionViewCacheConsumer.assignment().forEach(t -> {
//		        System.out.printf("Set %s to offset 0%n", t.toString());
//		        opinionViewCacheConsumer.seek(t, 0);
//		    });
            TopicPartition topicPartition = new TopicPartition(kafkaProperties.getOpinionViewCacheTopic(), 0);
            List<TopicPartition> partitions = Arrays.asList(topicPartition);
            opinionViewCacheConsumer.assign(partitions);
            opinionViewCacheConsumer.seekToBeginning(partitions);
            while (true) {
                ConsumerRecords<Integer, OpinionViewMessage> opinionViewMessageRecords = opinionViewCacheConsumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<Integer, OpinionViewMessage> opinionViewMessageRecord : opinionViewMessageRecords) {
//		        	log.info("topic = {}, partition = {}, offset = {}, record key = {}, record value length = {}",
//		        			opinionViewMessageRecord.topic(), opinionViewMessageRecord.partition(), opinionViewMessageRecord.offset());
                    log.info("opinionViewMessageRecord: {}", opinionViewMessageRecord);
                    OpinionViewMessage opinionViewMessage = opinionViewMessageRecord.value();
                    log.info("opinionViewMessage: {}", opinionViewMessage);
                    if ( opinionViewMessage == null ) continue;
                    if ( opinionViewMessage.getOpinionView().isPresent() ) {
                        opinionViewData.addOpinionView(opinionViewMessage.getOpinionView().get());
                    }
                    if ( opinionViewMessage.getCaseListEntry().isPresent() ) {
                        CaseListEntry caseListEntry = opinionViewMessage.getCaseListEntry().get();
                        SlipOpinion slipOpinion = new SlipOpinion(caseListEntry.getFileName(), caseListEntry.getFileExtension(), caseListEntry.getTitle(), caseListEntry.getOpinionDate(), caseListEntry.getCourt(), caseListEntry.getSearchUrl());
                        opinionViewData.deleteOpinionView(slipOpinion.getOpinionKey());
                    }
                }
            }
        } catch (WakeupException e) {
            log.error("WakeupException: {}", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e);
        } finally {
            opinionViewCacheConsumer.close();
        }
    }

    @Scheduled(fixedRate = 86400000, initialDelay = 10000)
    public String processEmails() throws SQLException {
//        try ( Connection con = dataSource.getConnection()) {
//            String emailUserNeeded = slipOpininScraperDao.callEmailUserNeeded(con);
//            if ( emailUserNeeded != null && emailUserNeeded.equalsIgnoreCase("NOEMAIL")) {
//                return "NOEMAIL";
//            }
//        }
//        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
//        int minusDays = 7 + dayOfWeek % 7;
//        LocalDate pastDate = LocalDate.now().minusDays(minusDays);
        LocalDate pastDate = LocalDate.of(2021, 02, 14);
        log.info("pastDate: {}", pastDate);
        for ( OpinionView opinionView: opinionViewData.getOpinionViews(pastDate) ) {
            log.info("OpinionView: {}", opinionView);
        }
        return "EMAIL";
    }
}
