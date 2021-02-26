package update;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import opca.dao.OpinionBaseDao;
import opca.model.OpinionBase;
import opca.model.SlipOpinion;
import opca.parser.OpinionScraperInterface;
import opca.parser.ParsedOpinionCitationSet;
import opca.scraper.TestCAParseSlipDetails;
import opca.service.CAOnlineParseAndView;
import opca.view.OpinionView;
import opca.view.OpinionViewBuilder;
import statutes.service.StatutesService;
import statutes.service.client.StatutesServiceClientImpl;

@SpringBootApplication(scanBasePackages = {"opca", "update"})
@ConditionalOnProperty(name = "TestParseAndView.active", havingValue = "true", matchIfMissing = false)
@EnableTransactionManagement
public class TestParseAndView implements ApplicationRunner {
	Logger logger = LoggerFactory.getLogger(TestParseAndView.class);
	public static void main(String... args) {
		new SpringApplicationBuilder(TestParseAndView.class).run(args);
	}

	@Autowired
	private CAOnlineParseAndView parseAndView;
	@Autowired
	private OpinionBaseDao opinionBaseDao;
	@Override
	public void run(ApplicationArguments args) {

		StatutesService statutesService = new StatutesServiceClientImpl("http://localhost:8090/");
//				OpinionScraperInterface caseScraper = new CACaseScraper(true);
//				OpinionScraperInterface caseScraper = new TestCACaseScraper(false);
		OpinionScraperInterface caseScraper = new TestCAParseSlipDetails(false);
		SlipOpinion slipOpinion = parseAndView.getSlipOpinion(caseScraper, statutesService);
		
	
		OpinionViewBuilder opinionViewBuilder = new OpinionViewBuilder(statutesService);
	
		List<OpinionBase> opinionsWithReferringOpinions = opinionBaseDao.opinionsWithReferringOpinions(slipOpinion.getOpinionCitations()
				.stream()
				.map(OpinionBase::getOpinionKey)
				.collect(Collectors.toList()));
		
		slipOpinion.getOpinionCitations().clear();
		slipOpinion.getOpinionCitations().addAll(opinionsWithReferringOpinions);

		System.out.println("slipOpinion:= " 
				+ slipOpinion.getTitle() 
				+ "\n	:OpinionKey= " + slipOpinion.getOpinionKey()
				+ "\n	:OpinionCitations().size()= " + (slipOpinion.getOpinionCitations()== null?"xx":slipOpinion.getOpinionCitations().size())
				+ "\n	:StatuteCitations().size()= " + (slipOpinion.getStatuteCitations()== null?"xx":slipOpinion.getStatuteCitations().size())
				+ "\n	:CountReferringOpinions= " + slipOpinion.getCountReferringOpinions()
			);
		for ( OpinionBase opinionCitation: slipOpinion.getOpinionCitations()) {
			System.out.println("\nopinionCitation:= " 
					+ opinionCitation.getTitle() 
					+ "\n		:OpinionKey= " + opinionCitation.getOpinionKey()
					+ "\n		:CountReferringOpinions= " + opinionCitation.getCountReferringOpinions()
				);
		}

		ParsedOpinionCitationSet parserResults = new ParsedOpinionCitationSet(slipOpinion);
		OpinionView opinionView = opinionViewBuilder.buildOpinionView(slipOpinion, parserResults);
		System.out.println("OpinionView" + opinionView);
		System.out.println("Completed");

//			List<ScrapedOpinionDocument> scrapedCases = caseScraper.scrapeOpinionFiles(caseScraper.getCaseList());
//			scrapedCases.stream().forEach(sc->{
//				System.out.println(sc.getOpinionBase());	
//			});
			
	}
}