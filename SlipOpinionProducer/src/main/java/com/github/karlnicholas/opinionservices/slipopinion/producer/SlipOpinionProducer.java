package com.github.karlnicholas.opinionservices.slipopinion.producer;

import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import opca.model.SlipOpinion;
import opca.parser.OpinionScraperInterface;
import opca.scraper.TestCAParseSlipDetails;
import opinions.service.OpinionsService;
import opinions.service.client.OpinionsServiceClientImpl;
import statutes.StatutesTitles;
import statutes.service.StatutesService;
import statutes.service.client.StatutesServiceClientImpl;

@SpringBootApplication(scanBasePackages = {"opca"})
@EnableScheduling
public class SlipOpinionProducer {
	Logger logger = LoggerFactory.getLogger(SlipOpinionProducer.class);
	public static void main(String... args) {
		new SpringApplicationBuilder(SlipOpinionProducer.class).run(args);
	}

}