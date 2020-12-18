package update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import opca.parser.OpinionScraperInterface;
import opca.scraper.TestCAParseSlipDetails;
import opca.service.CAOnlineUpdates;
import statutes.service.BlockingStatutesService;
import statutes.service.client.BlockingStatutesServiceClientImpl;

@SpringBootApplication(scanBasePackages = {"opca", "update"})
@EnableJpaRepositories(basePackages = {"opca"})
@EnableTransactionManagement
public class TestOnlineUpdates implements ApplicationRunner {
	public static void main(String... args) {
		new SpringApplicationBuilder(TestOnlineUpdates.class).run(args);
	}

	@Autowired
	private CAOnlineUpdates caOnlineUpdates;
	@Override
	public void run(ApplicationArguments args) {

		BlockingStatutesService blockingStatutesService = new BlockingStatutesServiceClientImpl("http://localhost:8090/");
//				OpinionScraperInterface caseScraper = new CACaseScraper(true);
//				OpinionScraperInterface caseScraper = new TestCACaseScraper(false);
		OpinionScraperInterface caseScraper = new TestCAParseSlipDetails(false);
		caOnlineUpdates.updateDatabase(caseScraper, blockingStatutesService);

//			List<ScrapedOpinionDocument> scrapedCases = caseScraper.scrapeOpinionFiles(caseScraper.getCaseList());
//			scrapedCases.stream().forEach(sc->{
//				System.out.println(sc.getOpinionBase());	
//			});
			
	}
}