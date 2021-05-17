package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.caselist.model.CaseListEntries;
import com.github.karlnicholas.legalservices.opinion.service.OpinionService;
import com.github.karlnicholas.legalservices.opinion.service.OpinionServiceFactory;
import com.github.karlnicholas.legalservices.slipopinion.parser.OpinionScraperInterface;
import com.github.karlnicholas.legalservices.slipopinion.scraper.TestCAParseSlipDetails;

@Component
public class CaseListScraperComponent {
	private final Logger log = LoggerFactory.getLogger(CaseListScraperComponent.class);
	private final OpinionScraperInterface caseScraper;
	private final ObjectMapper objectMapper;
	private final OpinionService opinionService;
	private final Producer<Integer, JsonNode> producer;
	private final KakfaProperties kafkaProperties;

	public CaseListScraperComponent(ObjectMapper objectMapper, 
			Producer<Integer, JsonNode> producer, 
			KakfaProperties kafkaProperties
	) {
	    this.objectMapper = objectMapper;
	    this.producer = producer;
	    this.kafkaProperties = kafkaProperties;

		caseScraper = new TestCAParseSlipDetails(false);
//		caseScraper = new CACaseScraper(false);
	    opinionService = OpinionServiceFactory.getOpinionServiceClient(objectMapper);
		
	}


	@Scheduled(fixedRate = 3600000)
	public String reportCurrentTime() throws SQLException, IOException {
 		// use the transaction manager in the database for a cheap job manager
		ResponseEntity<String> response = opinionService.callSlipOpinionUpdateNeeded();
		if ( response.getStatusCodeValue() != 200 ) {
			log.error("opinionsService.callSlipOpinionUpdateNeeded() {}", response.getStatusCode());
			return "ERROR";
		}
		String slipOpinionUpdateNeeded = response.getBody();
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