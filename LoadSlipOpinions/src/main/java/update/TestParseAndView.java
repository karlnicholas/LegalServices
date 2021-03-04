package update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.SlipOpinion;
import opca.parser.OpinionScraperInterface;
import opca.parser.ScrapedOpinionDocument;
import opca.parser.SlipOpinionDocumentParser;
import opca.scraper.TestCAParseSlipDetails;
import opca.service.CAOnlineParseAndView;
import opca.view.OpinionView;
import opca.view.OpinionViewBuilder;
import opinions.service.OpinionsService;
import opinions.service.client.OpinionsServiceClientImpl;
import statutes.StatutesTitles;
import statutes.service.StatutesService;
import statutes.service.client.StatutesServiceClientImpl;

@SpringBootApplication(scanBasePackages = {"opca", "update"})
@ConditionalOnProperty(name = "TestParseAndView.active", havingValue = "true", matchIfMissing = false)
public class TestParseAndView implements ApplicationRunner {
	Logger logger = LoggerFactory.getLogger(TestParseAndView.class);
	public static void main(String... args) {
		new SpringApplicationBuilder(TestParseAndView.class).run(args);
	}

	@Autowired
	private ObjectMapper objectMapper;
	@Override
	public void run(ApplicationArguments args) throws JsonProcessingException {

		StatutesService statutesService = new StatutesServiceClientImpl("http://localhost:8090/");
		OpinionsService opinionsService = new OpinionsServiceClientImpl("http://localhost:8091/");
		OpinionViewBuilder opinionViewBuilder = new OpinionViewBuilder(statutesService);
		StatutesTitles[] arrayStatutesTitles = statutesService.getStatutesTitles().getBody();


		//				OpinionScraperInterface caseScraper = new CACaseScraper(true);
//				OpinionScraperInterface caseScraper = new TestCACaseScraper(false);
		OpinionScraperInterface caseScraper = new TestCAParseSlipDetails(false);

 		List<SlipOpinion> onlineOpinions = caseScraper.getCaseList();
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
		List<String> savedOpinions = StreamSupport.stream(Arrays.spliterator(opinionsService.getSlipOpinionsList().getBody().split(",")), false)
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

				SlipOpinionDocumentParser opinionDocumentParser = new SlipOpinionDocumentParser(arrayStatutesTitles);
				
				opinionDocumentParser.parseOpinionDocument(scrapedOpinionDocument, scrapedOpinionDocument.getOpinionBase());
				// maybe someday deal with court issued modifications
				opinionDocumentParser.parseSlipOpinionDetails((SlipOpinion) scrapedOpinionDocument.getOpinionBase(), scrapedOpinionDocument);
				System.out.println(slipOpinion);
		        JsonNode  jsonNode = objectMapper.valueToTree(slipOpinion);
		        System.out.println(jsonNode);
		        
		        try {
					SlipOpinion slipOpinionFromJson = objectMapper.treeToValue(jsonNode, SlipOpinion.class);
					parseAndPrintOpinion(opinionsService, opinionViewBuilder, arrayStatutesTitles, caseScraper, slipOpinionFromJson);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			});
		}
	//
//        SlipOpinion slipOpinion = onlineOpinions.get(0);
//
//        ScrapedOpinionDocument scrapedOpinionDocument = caseScraper.scrapeOpinionFile(slipOpinion);
//
//		SlipOpinionDocumentParser opinionDocumentParser = new SlipOpinionDocumentParser(arrayStatutesTitles);
//		
//		opinionDocumentParser.parseOpinionDocument(scrapedOpinionDocument, scrapedOpinionDocument.getOpinionBase());
//		// maybe someday deal with court issued modifications
//		opinionDocumentParser.parseSlipOpinionDetails((SlipOpinion) scrapedOpinionDocument.getOpinionBase(), scrapedOpinionDocument);
//		System.out.println(slipOpinion);
//		
//        JsonNode  jsonNode = objectMapper.valueToTree(slipOpinion);
//        
//        System.out.println(jsonNode);
//        
//        SlipOpinion aCase = objectMapper.treeToValue(jsonNode, SlipOpinion.class);
//        JsonNode  jsonNode2 = objectMapper.valueToTree(aCase);
//        System.out.println(jsonNode2);
			
	}
	private void parseAndPrintOpinion(OpinionsService opinionsService, OpinionViewBuilder opinionViewBuilder,
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
		
		List<OpinionBase> opinionsWithReferringOpinions = opinionsService.getOpinionsWithStatuteCitations(opinionKeys).getBody();

		slipOpinion.getOpinionCitations().clear();
		slipOpinion.getOpinionCitations().addAll(opinionsWithReferringOpinions);

		OpinionView opinionView = opinionViewBuilder.buildOpinionView(slipOpinion);
//		System.out.print(".");
//		System.out.println("Completed");
		System.out.println("opinionView:= " + opinionView.getTitle() 
			+ " : " + (opinionView.getCases()== null?"0":opinionView.getCases().size())
			+ " : " + (opinionView.getStatutes()== null?"0":opinionView.getSectionViews().size())
// 				+ " : " + opinionView.getCondensedCaseInfo()
			+ " : " + opinionView.getCondensedStatuteInfo()
		);
//		for ( CaseView caseView: opinionView.getCases().subList(0, opinionView.getCases().size() > 10 ? 10 : opinionView.getCases().size())) {
//			System.out.println("	caseView(" + caseView.getImportance() + ") : " + caseView.getTitle() + " (" + caseView.getCitation() +")" );
//		}
//		for ( SectionView sectionView: opinionView.getSectionViews()) {
//			System.out.println("	sectionView(" + sectionView.getImportance() + ") : " + sectionView.getShortTitle());
//		}
	}
}