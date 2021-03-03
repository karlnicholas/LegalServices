package com.github.karlnicholas.opinionservices.slipopinion.producer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import opca.model.SlipOpinion;
import opca.parser.OpinionScraperInterface;
import opca.scraper.TestCAParseSlipDetails;

@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private final OpinionScraperInterface caseScraper;
	private final ObjectMapper objectMapper;
	private final Producer<String, JsonNode> producer;
	

	public ScheduledTasks() {
		caseScraper = new TestCAParseSlipDetails(false);
	    objectMapper = new ObjectMapper();
        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.202.106:32369");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.ByteArraySerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.connect.json.JsonSerializer");

        producer = new KafkaProducer<>(configProperties);
	}


	@Scheduled(fixedRate = 5000)
	public void reportCurrentTime() {
		log.info("The time is now {}", dateFormat.format(new Date()));

 		List<SlipOpinion> onlineOpinions = caseScraper.getCaseList();
		
 		SlipOpinion slipOpinion = onlineOpinions.get(0);

        JsonNode  jsonNode = objectMapper.valueToTree(slipOpinion);
        ProducerRecord<String, JsonNode> rec = new ProducerRecord<String, JsonNode>("slipOpinions",jsonNode);
        producer.send(rec);
        
	}
}