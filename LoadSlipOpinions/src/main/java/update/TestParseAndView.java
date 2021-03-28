package update;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;
import com.github.karlnicholas.legalservices.opinion.parser.ScrapedOpinionDocument;
import com.github.karlnicholas.legalservices.opinion.service.OpinionService;
import com.github.karlnicholas.legalservices.opinion.service.OpinionServiceFactory;
import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;
import com.github.karlnicholas.legalservices.opinionview.model.OpinionViewBuilder;
import com.github.karlnicholas.legalservices.slipopinion.model.SlipOpinion;
import com.github.karlnicholas.legalservices.slipopinion.parser.OpinionScraperInterface;
import com.github.karlnicholas.legalservices.slipopinion.parser.SlipOpinionDocumentParser;
import com.github.karlnicholas.legalservices.slipopinion.scraper.TestCAParseSlipDetails;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;
import com.github.karlnicholas.legalservices.statute.service.StatuteService;
import com.github.karlnicholas.legalservices.statute.service.StatutesServiceFactory;
import com.mysql.cj.jdbc.MysqlDataSource;

@SpringBootApplication(scanBasePackages = {"opca", "update"})
@ConditionalOnProperty(name = "TestParseAndView.active", havingValue = "true", matchIfMissing = false)
public class TestParseAndView implements ApplicationRunner {
	Logger logger = LoggerFactory.getLogger(TestParseAndView.class);
	public static void main(String... args) {
		new SpringApplicationBuilder(TestParseAndView.class).run(args);
	}

	@Override
	public void run(ApplicationArguments args) throws JsonProcessingException, SQLException {

		StatuteService statutesService = StatutesServiceFactory.getStatutesServiceClient();
		OpinionService opinionService = OpinionServiceFactory.getOpinionServiceClient();
		OpinionViewBuilder opinionViewBuilder = new OpinionViewBuilder(statutesService);
		StatutesTitles[] arrayStatutesTitles = statutesService.getStatutesTitles().getBody();
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUrl("jdbc:mysql://localhost:3306/op");
		dataSource.setUser("op");
		dataSource.setPassword("op");
		 
		//				OpinionScraperInterface caseScraper = new CACaseScraper(true);
//				OpinionScraperInterface caseScraper = new TestCACaseScraper(false);
		OpinionScraperInterface caseScraper = new TestCAParseSlipDetails(false);

 		List<SlipOpinion> onlineOpinions = caseScraper.getCaseList();
// 		onlineOpinions.forEach(so->System.out.println(so.getOpinionDate()+","));
 		SlipOpinion slipOpinionP = onlineOpinions.get(0);
		parseAndPrintOpinion(opinionService, opinionViewBuilder, arrayStatutesTitles, caseScraper, slipOpinionP);
// 		for ( SlipOpinion slipOpinion: onlineOpinions) {
// 			parseAndPrintOpinion(opinionsService, opinionViewBuilder, arrayStatutesTitles, caseScraper, slipOpinion);
// 		}
//		parseAndPrintOpinion(opinionsService, opinionViewBuilder, arrayStatutesTitles, caseScraper, onlineOpinions.get(209));
// 		onlineOpinions.parallelStream().forEach(slipOpinion->parseAndPrintOpinion(opinionsService, opinionViewBuilder, arrayStatutesTitles, caseScraper, slipOpinion));

		List<String> foundOpinions = onlineOpinions
				.stream()
				.map(SlipOpinion::getFileName)
				.collect(Collectors.toList());
// 		onlineOpinions.str
		StringBuilder sb = new StringBuilder();
		foundOpinions.forEach(f->{
			sb.append(f);
			sb.append(',');
		});
		// use the transaction manager in the database for a cheap job manager
		ResponseEntity<String> response = opinionService.callSlipOpinionUpdateNeeded();
		if ( response.getStatusCodeValue() != 200 ) {
			logger.error("opinionsService.callSlipOpinionUpdateNeeded() {}", response.getStatusCode());
			return;
		}
		String slipOpinionsList = response.getBody();
		if ( slipOpinionsList.equalsIgnoreCase("NOUPDATE")) {
			return;
		}
		List<String> savedOpinions = StreamSupport.stream(Arrays.spliterator(
				slipOpinionsList.split(",")), false)
		.collect(Collectors.toList());
		
		List<String> newOpinions = new ArrayList<>(foundOpinions);
		newOpinions.removeAll(savedOpinions);
		if ( newOpinions.size() > 0 ) {
			opinionService.updateSlipOpinionList(sb.toString());
			List<SlipOpinion> lt = onlineOpinions
					.stream()
					.filter(slipOpinion->newOpinions.contains(slipOpinion.getFileName()))
					.collect(Collectors.toList());

			lt.forEach(slipOpinion->{
		        ScrapedOpinionDocument scrapedOpinionDocument = caseScraper.scrapeOpinionFile(slipOpinion);

				SlipOpinionDocumentParser opinionDocumentParser = new SlipOpinionDocumentParser(arrayStatutesTitles);
				
				opinionDocumentParser.parseOpinionDocument(scrapedOpinionDocument, scrapedOpinionDocument.getOpinionBase());
				// maybe someday deal with court issued modifications
				opinionDocumentParser.parseSlipOpinionDetails((SlipOpinion) scrapedOpinionDocument.getOpinionBase(), scrapedOpinionDocument);
				System.out.println(slipOpinion);
//		        JsonNode  jsonNode = objectMapper.valueToTree(slipOpinion);
//		        System.out.println(jsonNode);
		        
//				OpinionView opinionView = parseAndPrintOpinion(opinionsService, opinionViewBuilder, arrayStatutesTitles, caseScraper, slipOpinion);
//				OpinionViewSerializer opinionViewSerializer = new OpinionViewSerializer();
//				byte[] opinionViewBytes = opinionViewSerializer.serialize(opinionView);
//System.out.println(opinionViewBytes.length);
//				OpinionViewDeserializer opinionViewDeserializer = new OpinionViewDeserializer();
//				OpinionView opinionViewDes = opinionViewDeserializer.deserialize(opinionViewBytes);
//		        try {
////					SlipOpinion slipOpinionFromJson = objectMapper.treeToValue(jsonNode, SlipOpinion.class);
//					OpinionView opinionView = parseAndPrintOpinion(opinionsService, opinionViewBuilder, arrayStatutesTitles, caseScraper, slipOpinion);
//					OpinionViewKafkaDto opinionViewKafkaDto = new OpinionViewKafkaDto(opinionView); 
////					System.out.print(".");
////					System.out.println("Completed");
//			        JsonNode opinionViewKafkaDtoNode = objectMapper.valueToTree(opinionViewKafkaDto);
//					System.out.println(opinionViewKafkaDtoNode);
//					OpinionViewKafkaDto opinionViewKafkaDtoNodeFromJson = objectMapper.treeToValue(opinionViewKafkaDtoNode, OpinionViewKafkaDto.class);
//					System.out.println(opinionViewKafkaDtoNodeFromJson);
////					System.out.println("opinionView:= " + opinionViewFromJson.getTitle() 
////						+ " : " + (opinionView.getCases()== null?"0":opinionViewFromJson.getCases().size())
////						+ " : " + (opinionView.getStatutes()== null?"0":opinionViewFromJson.getSectionViews().size())
//////			 				+ " : " + opinionView.getCondensedCaseInfo()
////						+ " : " + opinionViewFromJson.getCondensedStatuteInfo()
////					);
//				} catch (JsonProcessingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
			});
		}
	}

	//
//  SlipOpinion slipOpinion = onlineOpinions.get(0);
//
//  ScrapedOpinionDocument scrapedOpinionDocument = caseScraper.scrapeOpinionFile(slipOpinion);
//
//	SlipOpinionDocumentParser opinionDocumentParser = new SlipOpinionDocumentParser(arrayStatutesTitles);
//	
//	opinionDocumentParser.parseOpinionDocument(scrapedOpinionDocument, scrapedOpinionDocument.getOpinionBase());
//	// maybe someday deal with court issued modifications
//	opinionDocumentParser.parseSlipOpinionDetails((SlipOpinion) scrapedOpinionDocument.getOpinionBase(), scrapedOpinionDocument);
//	System.out.println(slipOpinion);
//	
//  JsonNode  jsonNode = objectMapper.valueToTree(slipOpinion);
//  
//  System.out.println(jsonNode);
//  
//  SlipOpinion aCase = objectMapper.treeToValue(jsonNode, SlipOpinion.class);
//  JsonNode  jsonNode2 = objectMapper.valueToTree(aCase);
//  System.out.println(jsonNode2);
		
	private OpinionView parseAndPrintOpinion(OpinionService opinionService, OpinionViewBuilder opinionViewBuilder,
			StatutesTitles[] arrayStatutesTitles, OpinionScraperInterface caseScraper, SlipOpinion slipOpinion) {
		// no retries
//		parseAndView.processCase(slipOpinion, caseScraper, arrayStatutesTitles);
		// Create the CACodes list
		ScrapedOpinionDocument scrapedOpinionDocument = caseScraper.scrapeOpinionFile(slipOpinion);

		SlipOpinionDocumentParser opinionDocumentParser = new SlipOpinionDocumentParser(arrayStatutesTitles);
		
		opinionDocumentParser.parseOpinionDocument(scrapedOpinionDocument, scrapedOpinionDocument.getOpinionBase());
		// maybe someday deal with court issued modifications
		opinionDocumentParser.parseSlipOpinionDetails((SlipOpinion) scrapedOpinionDocument.getOpinionBase(), scrapedOpinionDocument);
		
		List<OpinionKey> opinionKeys = slipOpinion.getOpinionCitations()
				.stream()
				.map(OpinionBase::getOpinionKey)
				.collect(Collectors.toList());
		
		List<OpinionBase> opinionsWithReferringOpinions = opinionService.getOpinionsWithStatuteCitations(opinionKeys).getBody();

		slipOpinion.getOpinionCitations().clear();
		slipOpinion.getOpinionCitations().addAll(opinionsWithReferringOpinions);

		OpinionView opinionView = opinionViewBuilder.buildOpinionView(slipOpinion);
		return opinionView;
	}
}