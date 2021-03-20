package loadnew;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.opinion.model.DTYPES;
import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;
import com.github.karlnicholas.legalservices.opinion.parser.OpinionDocumentsParser;
import com.github.karlnicholas.legalservices.opinion.parser.ParsedOpinionCitationSet;
import com.github.karlnicholas.legalservices.opinion.parser.ScrapedOpinionDocument;
import com.github.karlnicholas.legalservices.statute.api.IStatuteApi;

import loadmodelnew.LoadOpinionNew;

/**
 * Create new OpinionSummaries from LoadOpinion types, add to citationStore.
 * Merging?
 * 
 * @author karl
 *
 */
public class BuildCitationStore implements Runnable {
	List<LoadOpinionNew> clOps;
	CitationStore citationStore;
	private final OpinionDocumentsParser parser;

	public BuildCitationStore(List<LoadOpinionNew> clOps, CitationStore persistence, IStatuteApi iStatutesApi) {
		this.clOps = clOps;
		this.citationStore = persistence;
		parser = new OpinionDocumentsParser(iStatutesApi.getStatutesTitles());
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
//				synchronized ( citationStore ) {
//		        	OpinionBase existingOpinion = citationStore.opinionExists(opinionBase);
//		            if ( existingOpinion != null ) {
//		            	opinionBase = existingOpinion;
//		            }
//				}
				//
				ScrapedOpinionDocument parserDocument = new ScrapedOpinionDocument(opinionBase);
				parserDocument.setFootnotes( footnotes );
				parserDocument.setParagraphs( paragraphs );

				// not efficient, but it works for loading
				// if you are going to change it then watch for lower than the correct number of 
				// opinions and statutes loaded
//				synchronized ( citationStore ) {
					ParsedOpinionCitationSet parserResults = parser.parseOpinionDocuments(parserDocument, opinionBase, citationStore);
		        	OpinionBase existingOpinion = citationStore.opinionExists(opinionBase);
		            if ( existingOpinion != null ) {
		            	System.out.println("Existing opinion: " + opinionBase);
		            	System.out.println("                : " + existingOpinion);
		            } else {
						citationStore.persistOpinion(opinionBase);
						citationStore.mergeParsedDocumentCitations(opinionBase, parserResults);
		            }
//					System.out.println( opinionSummary.fullPrint() );
//				}
			}
		}
	}
}
