package update;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.SlipOpinion;
import opca.parser.OpinionScraperInterface;
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
	private CAOnlineParseAndView parseAndView;
	@Override
	public void run(ApplicationArguments args) {

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
 		onlineOpinions.parallelStream().forEach(slipOpinion->parseAndPrintOpinion(opinionsService, opinionViewBuilder, arrayStatutesTitles, caseScraper, slipOpinion));

	//
			
	}
	private void parseAndPrintOpinion(OpinionsService opinionsService, OpinionViewBuilder opinionViewBuilder,
			StatutesTitles[] arrayStatutesTitles, OpinionScraperInterface caseScraper, SlipOpinion slipOpinion) {
		// no retries
		parseAndView.processCase(slipOpinion, caseScraper, arrayStatutesTitles);
		
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