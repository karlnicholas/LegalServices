package com.github.karlnicholas.legalservices.opinionview.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.karlnicholas.legalservices.opinion.parser.ScrapedOpinionDocument;
import com.github.karlnicholas.legalservices.slipopinion.model.SlipOpinion;
import com.github.karlnicholas.legalservices.slipopinion.parser.OpinionScraperInterface;
import com.github.karlnicholas.legalservices.slipopinion.parser.SlipOpinionDocumentParser;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;

/**
 * 
 * @author karl
 *
 */
@Service
public class CAOnlineParseAndView {	
	Logger logger = LoggerFactory.getLogger(CAOnlineParseAndView.class);
	

	public void processCase(SlipOpinion slipOpinion, OpinionScraperInterface opinionScraper, StatutesTitles[] arrayStatutesTitles) {

		// Create the CACodes list
		ScrapedOpinionDocument scrapedOpinionDocument = opinionScraper.scrapeOpinionFile(slipOpinion);

		SlipOpinionDocumentParser opinionDocumentParser = new SlipOpinionDocumentParser(arrayStatutesTitles);
		
		opinionDocumentParser.parseOpinionDocument(scrapedOpinionDocument, scrapedOpinionDocument.getOpinionBase());
		// maybe someday deal with court issued modifications
		opinionDocumentParser.parseSlipOpinionDetails((SlipOpinion) scrapedOpinionDocument.getOpinionBase(), scrapedOpinionDocument);

//		OpinionBase opinionBase = scrapedOpinionDocument.getOpinionBase();
//		if ( logger.isDebugEnabled() ) {
//			logger.debug("scrapedOpinionDocument:= " 
//				+ scrapedOpinionDocument.getOpinionBase().getTitle() 
//				+ "\n	:OpinionKey= " + opinionBase.getOpinionKey()
//				+ "\n	:CountReferringOpinions= " + opinionBase.getCountReferringOpinions()
//				+ "\n	:ReferringOpinions.size()= " + (opinionBase.getReferringOpinions()== null?"xx":opinionBase.getReferringOpinions().size())
//				+ "\n	:OpinionCitations().size()= " + (opinionBase.getOpinionCitations()== null?"xx":opinionBase.getOpinionCitations().size())
//				+ "\n	:StatuteCitations().size()= " + (opinionBase.getStatuteCitations()== null?"xx":opinionBase.getStatuteCitations().size())
//				+ "\n	:Paragraphs().size()= " + (scrapedOpinionDocument.getParagraphs()== null?"xx":scrapedOpinionDocument.getParagraphs().size())
//				+ "\n	:Footnotes().size()= " + (scrapedOpinionDocument.getFootnotes()== null?"xx":scrapedOpinionDocument.getFootnotes().size())
//				+ "\n	:OpinionTable= " + parsedOpinionResults.getOpinionTable().size()
//				+ "\n	:StatuteTable= " + parsedOpinionResults.getStatuteTable().size()
//			);
//		}
	}

}
