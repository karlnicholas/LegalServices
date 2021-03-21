package loadnew;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.opinion.model.DTYPES;
import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;
import com.github.karlnicholas.legalservices.opinion.parser.OpinionDocumentParser;
import com.github.karlnicholas.legalservices.opinion.parser.ScrapedOpinionDocument;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;

import loadmodelnew.LoadOpinionNew;

/**
 * Create new OpinionSummaries from LoadOpinion types, add to citationStore.
 * Merging?
 * 
 * @author karl
 *
 */
public class BuildCitationStore implements Runnable {
	private final List<LoadOpinionNew> clOps;
	private final CitationStore citationStore;
	private final OpinionDocumentParser parser;

	public BuildCitationStore(List<LoadOpinionNew> clOps, CitationStore persistence, StatutesTitles[] statutesTitles) {
		this.clOps = clOps;
		this.citationStore = persistence;
		parser = new OpinionDocumentParser(statutesTitles);
	}

	@Override
	public void run() {
//		for (LoadOpinion op : clOps) {
		int l = clOps.size();
		int i;
		for (i=0; i < l; ++i) {
			LoadOpinionNew op = clOps.get(i);
			Element opinionElement = op.getOpinionElement();
			Elements ps = opinionElement.getElementsByTag("p");
			List<String> paragraphs = new ArrayList<String>();
			List<String> footnotes = new ArrayList<String>();

			for (Element p : ps) {
				String text = p.text();
				if (text.length() == 0)
					continue;
				if (text.charAt(0) == '[' || text.charAt(0) == '(')
					footnotes.add(text);
				else {
					Elements bs = p.getElementsByTag("span");
					for ( Element b: bs) {
						b.remove();
					}
					paragraphs.add(p.text());
				}
			}
			String citation = op.getCitation();
			// if ( name != null && name.contains("Rptr.") ) name =
			// op.getCitation();
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
					continue;
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
	            		continue;
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
//					System.out.println( opinionSummary.fullPrint() );
//				}
			}
		}
	}

}
