package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.github.karlnicholas.legalservices.opinionview.dao.SlipOpininScraperDao;
import com.github.karlnicholas.legalservices.opinionview.kafka.KakfaProperties;
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
	private final DataSource dataSource;

	public CaseListScraperComponent(ObjectMapper objectMapper, 
			Producer<Integer, JsonNode> producer, 
			KakfaProperties kafkaProperties, 
			DataSource dataSource
	) {
	    this.objectMapper = objectMapper;
	    this.producer = producer;
	    this.dataSource = dataSource;
	    this.kafkaProperties = kafkaProperties;
	    String slipopinionprocessor = System.getenv("slipopinionprocessor");
	    if ( slipopinionprocessor != null && slipopinionprocessor.equalsIgnoreCase("production")) {
			caseScraper = new CACaseScraper(false);
	    } else {
			caseScraper = new TestCAParseSlipDetails(false);
	    }
		slipOpininScraperDao = new SlipOpininScraperDao();
	}


	@Scheduled(fixedRate = 3600000, initialDelay = 60000)
	public String reportCurrentTime() throws SQLException, IOException {
 		// use the transaction manager in the database for a cheap job manager
		try ( Connection con = dataSource.getConnection()) {
			String slipOpinionUpdateNeeded = slipOpininScraperDao.callSlipOpinionUpdateNeeded(con);
			if ( slipOpinionUpdateNeeded != null && slipOpinionUpdateNeeded.equalsIgnoreCase("NOUPDATE")) {
				return "NOUPDATE";
			}
		}
		// OK to proceed with pushing caseListEntries to kafka
		CaseListEntries caseListEntries = caseScraper.getCaseList();
		JsonNode  jsonNode = objectMapper.valueToTree(caseListEntries);
		ProducerRecord<Integer, JsonNode> rec = new ProducerRecord<>(kafkaProperties.getCaseListEntriesTopic(), jsonNode);
		producer.send(rec);
		return "POLLED";
	}
}