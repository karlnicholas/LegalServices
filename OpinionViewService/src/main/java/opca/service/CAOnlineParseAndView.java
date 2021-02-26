package opca.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.SlipOpinion;
import opca.parser.OpinionScraperInterface;
import opca.parser.ScrapedOpinionDocument;
import opca.parser.SlipOpinionDocumentParser;
import opca.parser.ParsedOpinionCitationSet;
import statutes.StatutesTitles;
import statutes.service.StatutesService;

/**
 * 
 * @author karl
 *
 */
@Service
public class CAOnlineParseAndView {	
	Logger logger = LoggerFactory.getLogger(CAOnlineParseAndView.class);
	
//	public void updateOpinionViews(List<OpinionKey> opinionKeys, BlockingStatutesService blockingStatutesService) {
//        opinionViewSingleton.updateOpinionViews(opinionKeys, blockingStatutesService);
//	}

	// @Transactional very important, won't work without it.
	public SlipOpinion getSlipOpinion(OpinionScraperInterface caseScraper, StatutesService statutesService) {
		
 		List<SlipOpinion> onlineOpinions = caseScraper.getCaseList();
 		// save OpinionKeys for cache handling 
		List<OpinionKey> opinionKeys = new ArrayList<>();
		for( SlipOpinion slipOpinion: onlineOpinions) {
			opinionKeys.add(slipOpinion.getOpinionKey());
		}
		if ( onlineOpinions == null || onlineOpinions.size() == 0 ) {
			logger.info("No cases found online: returning.");
//				return opinionKeys;
		}
		
		//
//			onlineOpinions.remove(0);
//			onlineOpinions = onlineOpinions.subList(0, 340);
//			onlineOpinions = onlineOpinions.subList(0, 0);
//			onlineOpinions = onlineOpinions.subList(0, 1);
//		onlineOpinions = onlineOpinions.subList(0,1);
//		int c = (int)(Math.random() * onlineOpinions.size());
//		System.out.println("C=" + c);
//		SlipOpinion slipOpinion = onlineOpinions.get(c);
		SlipOpinion slipOpinion = onlineOpinions.get(0);

	//
		// no retries
		processCase(slipOpinion, caseScraper, statutesService);
//			processAndPersistCases(onlineOpinions, caseScraper);
		return slipOpinion; 
	}
	
	private void processCase(SlipOpinion slipOpinion, OpinionScraperInterface opinionScraper, StatutesService statutesService) {

		// Create the CACodes list
		ScrapedOpinionDocument scrapedOpinionDocument = opinionScraper.scrapeOpinionFile(slipOpinion);

		StatutesTitles[] arrayStatutesTitles = statutesService.getStatutesTitles().getBody();

		SlipOpinionDocumentParser opinionDocumentParser = new SlipOpinionDocumentParser(arrayStatutesTitles);
		
		ParsedOpinionCitationSet parsedOpinionResults = opinionDocumentParser.parseOpinionDocument(scrapedOpinionDocument, scrapedOpinionDocument.getOpinionBase());
		// maybe someday deal with court issued modifications
		opinionDocumentParser.parseSlipOpinionDetails((SlipOpinion) scrapedOpinionDocument.getOpinionBase(), scrapedOpinionDocument);
		OpinionBase opinionBase = scrapedOpinionDocument.getOpinionBase();
		if ( logger.isInfoEnabled() ) {
			logger.info("scrapedOpinionDocument:= " 
				+ scrapedOpinionDocument.getOpinionBase().getTitle() 
				+ "\n	:OpinionKey= " + opinionBase.getOpinionKey()
				+ "\n	:CountReferringOpinions= " + opinionBase.getCountReferringOpinions()
				+ "\n	:ReferringOpinions.size()= " + (opinionBase.getReferringOpinions()== null?"xx":opinionBase.getReferringOpinions().size())
				+ "\n	:OpinionCitations().size()= " + (opinionBase.getOpinionCitations()== null?"xx":opinionBase.getOpinionCitations().size())
				+ "\n	:StatuteCitations().size()= " + (opinionBase.getStatuteCitations()== null?"xx":opinionBase.getStatuteCitations().size())
				+ "\n	:Paragraphs().size()= " + (scrapedOpinionDocument.getParagraphs()== null?"xx":scrapedOpinionDocument.getParagraphs().size())
				+ "\n	:Footnotes().size()= " + (scrapedOpinionDocument.getFootnotes()== null?"xx":scrapedOpinionDocument.getFootnotes().size())
				+ "\n	:OpinionTable= " + parsedOpinionResults.getOpinionTable().size()
				+ "\n	:StatuteTable= " + parsedOpinionResults.getStatuteTable().size()
			);
		}
	}

}
