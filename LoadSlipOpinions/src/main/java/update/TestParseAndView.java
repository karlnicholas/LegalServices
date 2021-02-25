package update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import opca.parser.OpinionScraperInterface;
import opca.scraper.TestCAParseSlipDetails;
import opca.service.CAOnlineParseAndView;
import statutes.service.StatutesService;
import statutes.service.client.StatutesServiceClientImpl;

@SpringBootApplication(scanBasePackages = {"opca", "update"})
@ConditionalOnProperty(name = "TestParseAndView.active", havingValue = "true", matchIfMissing = false)
@EnableTransactionManagement
public class TestParseAndView implements ApplicationRunner {
	public static void main(String... args) {
		new SpringApplicationBuilder(TestParseAndView.class).run(args);
	}

	@Autowired
	private CAOnlineParseAndView parseAndView;
	@Override
	public void run(ApplicationArguments args) {

		StatutesService statutesService = new StatutesServiceClientImpl("http://localhost:8090/");
//				OpinionScraperInterface caseScraper = new CACaseScraper(true);
//				OpinionScraperInterface caseScraper = new TestCACaseScraper(false);
		OpinionScraperInterface caseScraper = new TestCAParseSlipDetails(false);
		parseAndView.updateDatabase(caseScraper, statutesService);
		System.out.println("Completed");

//			List<ScrapedOpinionDocument> scrapedCases = caseScraper.scrapeOpinionFiles(caseScraper.getCaseList());
//			scrapedCases.stream().forEach(sc->{
//				System.out.println(sc.getOpinionBase());	
//			});
			
	}
}