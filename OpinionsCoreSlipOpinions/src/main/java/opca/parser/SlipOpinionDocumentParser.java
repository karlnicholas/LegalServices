package opca.parser;

import java.util.*;

import statutes.StatutesTitles;
import opca.model.*;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 5/29/12
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 * 
 */
public class SlipOpinionDocumentParser extends OpinionDocumentParser {
    public SlipOpinionDocumentParser(StatutesTitles[] codeTitles) {
    	super(codeTitles);
    }

    public void parseSlipOpinionDetails(
        	SlipOpinion slipOpinion, 
        	ScrapedOpinionDocument parserDocument
    	) {
        
        SentenceParser sentenceParser = new SentenceParser();
        Iterator<String> pit = parserDocument.getParagraphs().iterator();
        boolean summaryFound = false;
        
        while ( pit.hasNext() ) {

        	String paragraph = pit.next();
        	ArrayList<String> sentences = sentenceParser.stripSentences(paragraph);

        	// look for details
        	// after a summaryParagraph is found, don't check any further .. (might have to change) 
        	if ( !summaryFound ) {
        		summaryFound = checkDetails( slipOpinion, paragraph, sentences );
        	}
        }
    }

    private boolean checkDetails(
    	SlipOpinion slipOpinion, 
    	String paragraph, 
    	ArrayList<String> sentences 
    ) {
    	String trimmed = paragraph.trim();
    	String lower = trimmed.toLowerCase();
    	boolean processed = false;
		if ( (lower.contains("affirm") || lower.contains("reverse") ) 
				&& !(paragraph.trim().startsWith("appeal from") || paragraph.trim().startsWith("appeals from")) 
		) {
			processed = true;
			Iterator<String> sit = sentences.iterator();
			while (sit.hasNext()) {
				String sentence = sit.next().trim().toLowerCase();
				
				if ( (sentence.contains("affirm") || sentence.contains("reverse") ) 
					&& ( sentence.contains("we ")) 
				) {					
					// get five sentences ... 
					slipOpinion.getSlipProperties().setSummary( paragraph
							.replace('\n', ' ')
							.replace('\r', ' ')
							.replace('\u0002', ' ')
			                .replace('\u201D', '"')
			                .replace('\u201C', '"')
			                .replace('\u2018', '\'')
			                .replace('\u2019', '\'')
			                .replace('\u001E', '-')
							.replace('\u00A0', ' ')
							.replace('\u000B',  ' ')
						);
						
					StringTokenizer tok = new StringTokenizer(sentence);
					while (tok.hasMoreTokens()) {
						String token = tok.nextToken();
						token = token.replace(",", "").replace(".", "");
						if ( token.contains("affirm") || token.contains("reverse") ) {
							if ( token.contains("ed") ) {
								slipOpinion.getSlipProperties().setSummary(null);
							} else {
								StringBuffer disposition = new StringBuffer(token.trim().replace(",", "").replace(".", "").toLowerCase());
								disposition.setCharAt(0, disposition.substring(0, 1).toUpperCase().charAt(0));
								if ( slipOpinion.getSlipProperties().getDisposition() == null ) {
									slipOpinion.getSlipProperties().setDisposition( disposition.toString() );
								}
							}
							break;
						}	
					}
				}
			}
		}
		return processed;
    }
}
