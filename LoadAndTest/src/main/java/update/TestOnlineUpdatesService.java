package update;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import opca.parser.OpinionScraperInterface;
import opca.scraper.TestCAParseSlipDetails;
import opca.service.CAOnlineUpdates;
import opca.service.OpinionViewSingleton;
import statutes.service.StatutesService;
import statutes.service.client.StatutesServiceClientImpl;

@Service
public class TestOnlineUpdatesService {
//	private OpinionViewCache slipOpinionData;
	private final OpinionViewSingleton opinionViewSingleton; 
	
	public TestOnlineUpdatesService(OpinionViewSingleton opinionViewSingleton) {
		this.opinionViewSingleton = opinionViewSingleton;
	}

	@Transactional
	public void testOnlineUpdates() {
		StatutesService statutesService;
			statutesService = new StatutesServiceClientImpl("http://localhost:8080/statutesrs/rs/");
//			OpinionScraperInterface caseScraper = new CACaseScraper(true);
//			OpinionScraperInterface caseScraper = new TestCACaseScraper(false);
			OpinionScraperInterface caseScraper = new TestCAParseSlipDetails(false);
			new CAOnlineUpdates(opinionViewSingleton).updateDatabase(caseScraper, statutesService);
		
	}
}
