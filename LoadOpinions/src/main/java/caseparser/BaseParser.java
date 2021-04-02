package caseparser;

import java.util.Iterator;
import java.util.List;

import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.opinion.model.DTYPES;
import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;
import com.github.karlnicholas.legalservices.opinion.parser.OpinionDocumentParser;
import com.github.karlnicholas.legalservices.opinion.parser.ScrapedOpinionDocument;

import model.ParsedOpinion;

public class BaseParser {
	private final CitationStore citationStore;
	private final OpinionDocumentParser parser;

	public BaseParser(CitationStore citationStore, OpinionDocumentParser parser) {
		this.citationStore = citationStore;
		this.parser = parser;
	}

	protected void processOpinion(
			ParsedOpinion op, 
			List<String> paragraphs, 
			List<String> footnotes,
			String citation
	) {
		if (citation != null) {
			// name = name.toLowerCase().replace(". ",
			// ".").replace("app.", "App.").replace("cal.",
			// "Cal.").replace("supp.", "Supp.");
			
			String[] citations = citation.split(",");
			String finalCitation = citations[0];
			if ( !OpinionKey.testValidOpinionKey(finalCitation)) { 
				for ( int c = 1; c < citations.length; ++c) {
					if ( OpinionKey.testValidOpinionKey(citations[c].trim())) {
						finalCitation = citations[c].trim();
						break;
					}
				}
			}
			if ( !OpinionKey.testValidOpinionKey(finalCitation)) {
				System.out.println("Invalid citation " + citation);
				return;
			}				
			// deal with citation here
			OpinionBase opinionBase = new OpinionBase(DTYPES.OPINIONBASE, new OpinionKey(finalCitation), op.getCaseName(), op.getDateFiled(), "");
			//
			ScrapedOpinionDocument parserDocument = new ScrapedOpinionDocument(opinionBase);
			parserDocument.setFootnotes( footnotes );
			parserDocument.setParagraphs( paragraphs );

			parser.parseOpinionDocument(parserDocument, opinionBase);

		    // remove obviously opinionCitations that are not goo
			OpinionKey obok = opinionBase.getOpinionKey();
			Iterator<OpinionBase> coIt = opinionBase.getOpinionCitations().iterator();
			while ( coIt.hasNext() ) {
				OpinionBase citatedOpinion = coIt.next();
				OpinionKey cook = citatedOpinion.getOpinionKey();
				if ( obok.getVolume() < cook.getVolume() 
						&& obok.getVset() == cook.getVset()
				) {
					coIt.remove();
				}
				if ( obok.getVolume() == cook.getVolume() 
						&& obok.getVset() == cook.getVset() 
						&& obok.getPage() <= cook.getPage()
				) {
					coIt.remove();
				}
			}

			OpinionBase existingOpinion = citationStore.opinionExists(opinionBase);
		    if ( existingOpinion != null ) {
		    	if ( existingOpinion.getTitle().equalsIgnoreCase(opinionBase.getTitle())) {
		    		return;
		    	}
		    	// should be no duplicates, so just remove them.
		    	citationStore.getOpinionTable().remove(opinionBase);
//	            	System.out.println("existingOpinion: " + existingOpinion);
//	            	System.out.println("opinionBase    : " + opinionBase);
//	            	System.out.println();
		    	for ( OpinionBase opinionCitation: existingOpinion.getOpinionCitations() ) {
		        	// first look and see if the citation is a known "real" citation
		    		OpinionBase existingOpinionCited = citationStore.opinionExists(opinionCitation);
		            if (  existingOpinionCited != null ) {
		            	// add citations where they don't already exist.
		            	existingOpinionCited.removeReferringOpinion(existingOpinion);
		            } else {
		        		// then
		        		OpinionBase existingOpinionCitation = citationStore.opinionCitationExists(opinionCitation);
		                if (  existingOpinionCitation != null ) {
		                	existingOpinionCitation.removeReferringOpinion(existingOpinion);
		                }
		            }
		    	}
		    } else {
				citationStore.persistOpinion(opinionBase);
				citationStore.mergeParsedDocumentCitations(opinionBase);
		    }
		}
	}

}
