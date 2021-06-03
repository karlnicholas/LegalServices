package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.caselist.model.CaseListEntries;
import com.github.karlnicholas.legalservices.slipopinion.parser.OpinionScraperInterface;
import com.github.karlnicholas.legalservices.slipopinion.scraper.CACaseScraper;
import com.github.karlnicholas.legalservices.slipopinion.scraper.TestCAParseSlipDetails;

@Component
public class CaseListScraperComponent {
	private final OpinionScraperInterface caseScraper;
	private final ObjectMapper objectMapper;
	private final SlipOpininScraperDao slipOpininScraperDao;
	private final Producer<Integer, JsonNode> producer;
	private final KakfaProperties kafkaProperties;

	public CaseListScraperComponent(ObjectMapper objectMapper, 
			Producer<Integer, JsonNode> producer, 
			KakfaProperties kafkaProperties, 
			DataSource dataSource
	) {
	    this.objectMapper = objectMapper;
	    this.producer = producer;
	    this.kafkaProperties = kafkaProperties;

//		caseScraper = new TestCAParseSlipDetails(false);
		caseScraper = new CACaseScraper(false);
		slipOpininScraperDao = new SlipOpininScraperDao(dataSource);
	}


	@Scheduled(fixedRate = 3600000, initialDelay = 60000)
	public String reportCurrentTime() throws SQLException, IOException {
 		// use the transaction manager in the database for a cheap job manager
		String slipOpinionUpdateNeeded = slipOpininScraperDao.callSlipOpinionUpdateNeeded();
		if ( slipOpinionUpdateNeeded != null && slipOpinionUpdateNeeded.equalsIgnoreCase("NOUPDATE")) {
			return "NOUPDATE";
		}
		// OK to proceed with pushing caseListEntries to kafka
		CaseListEntries caseListEntries = caseScraper.getCaseList();
	    JsonNode  jsonNode = objectMapper.valueToTree(caseListEntries);
	    ProducerRecord<Integer, JsonNode> rec = new ProducerRecord<>(kafkaProperties.getCaseListEntriesTopic(), jsonNode);
	    producer.send(rec);
		return "POLLED";
	}
}