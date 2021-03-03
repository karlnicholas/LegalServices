package com.github.karlnicholas.opinionservices.slipopinion.producer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
import opca.parser.ScrapedOpinionDocument;
import opca.parser.SlipOpinionDocumentParser;
import opca.scraper.TestCAParseSlipDetails;
import opinions.service.OpinionsService;
import opinions.service.client.OpinionsServiceClientImpl;
import statutes.service.StatutesService;
import statutes.service.client.StatutesServiceClientImpl;

@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private final OpinionScraperInterface caseScraper;
	private final ObjectMapper objectMapper;
	private final Producer<String, JsonNode> producer;
	private final OpinionsService opinionsService;
	private final SlipOpinionDocumentParser opinionDocumentParser;

	public ScheduledTasks(ObjectMapper objectMapper) {
		caseScraper = new TestCAParseSlipDetails(false);
	    this.objectMapper = objectMapper;

	    opinionsService = new OpinionsServiceClientImpl("http://localhost:8091/");

	    StatutesService statutesService = new StatutesServiceClientImpl("http://localhost:8090/");
		opinionDocumentParser = new SlipOpinionDocumentParser(statutesService.getStatutesTitles().getBody());
		
        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.202.109:32369");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.ByteArraySerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.connect.json.JsonSerializer");
        
        producer = new KafkaProducer<>(configProperties);
	}


	@Scheduled(fixedRate = 5000)
	public void reportCurrentTime() {
		log.debug("The time is now {}", dateFormat.format(new Date()));

 		List<SlipOpinion> onlineOpinions = caseScraper.getCaseList();

 		List<String> foundOpinions = onlineOpinions
				.stream()
				.map(SlipOpinion::getFileName)
				.collect(Collectors.toList());

 		// build a list of names
		StringBuilder sb = new StringBuilder();
		foundOpinions.forEach(f->{
			sb.append(f);
			sb.append(',');
		});
		List<String> savedOpinions = 
				StreamSupport.stream(Arrays.spliterator(opinionsService.getSlipOpinionsList().getBody().split(",")), false)
				.collect(Collectors.toList());
		
		List<String> newOpinions = new ArrayList<>(foundOpinions);
		newOpinions.removeAll(savedOpinions);
		if ( newOpinions.size() > 0 ) {
			opinionsService.updateSlipOpinionsList(sb.toString());

			List<SlipOpinion> lt = onlineOpinions
					.stream()
					.filter(slipOpinion->newOpinions.contains(slipOpinion.getFileName()))
					.collect(Collectors.toList());

	        lt.forEach(slipOpinion->{

	            ScrapedOpinionDocument scrapedOpinionDocument = caseScraper.scrapeOpinionFile(slipOpinion);

	    		opinionDocumentParser.parseOpinionDocument(scrapedOpinionDocument, scrapedOpinionDocument.getOpinionBase());
	    		// maybe someday deal with court issued modifications
	    		opinionDocumentParser.parseSlipOpinionDetails((SlipOpinion) scrapedOpinionDocument.getOpinionBase(), scrapedOpinionDocument);
	    		
	            JsonNode  jsonNode = objectMapper.valueToTree(slipOpinion);
	            	        	
		        ProducerRecord<String, JsonNode> rec = new ProducerRecord<String, JsonNode>("slipopinions",jsonNode);
		        producer.send(rec);
				log.info("producer {}", slipOpinion);
	        });

		}
		
	}
}