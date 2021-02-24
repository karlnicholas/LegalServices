package load;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import loadmodel.LoadOpinion;
import opca.memorydb.CitationStore;
import opca.model.DTYPES;
import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.parser.OpinionDocumentParser;
import opca.parser.ParsedOpinionCitationSet;
import opca.parser.ScrapedOpinionDocument;
import statutes.api.IStatutesApi;

/**
 * Create new OpinionSummaries from LoadOpinion types, add to citationStore.
 * Merging?
 * 
 * @author karl
 *
 */
public class BuildCitationStore implements Runnable {
	List<LoadOpinion> clOps;
	CitationStore citationStore;
	private final OpinionDocumentParser parser;

	public BuildCitationStore(List<LoadOpinion> clOps, CitationStore persistence, IStatutesApi iStatutesApi) {
		this.clOps = clOps;
		this.citationStore = persistence;
		parser = new OpinionDocumentParser(iStatutesApi.getStatutesTitles());
	}

	@Override
	public void run() {
//		for (LoadOpinion op : clOps) {
		int l = clOps.size();
		int i;
		for (i=0; i < l; ++i) {
			LoadOpinion op = clOps.get(i);
			Document lawBox = Parser.parse(op.getHtml_lawbox(), "");
			Elements ps = lawBox.getElementsByTag("p");
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
			String name = op.getCitation();
			// if ( name != null && name.contains("Rptr.") ) name =
			// op.getCitation();
			if (name != null) {
				// name = name.toLowerCase().replace(". ",
				// ".").replace("app.", "App.").replace("cal.",
				// "Cal.").replace("supp.", "Supp.");
				OpinionBase opinionBase = new OpinionBase(DTYPES.OPINIONBASE, new OpinionKey(name), op.getCaseName(), op.getDateFiled(), "");
				//
	        	OpinionBase existingOpinion = citationStore.findOpinionByOpinion(opinionBase);
	            if ( existingOpinion != null ) {
	            	opinionBase = existingOpinion;
	            }
				//
				ScrapedOpinionDocument parserDocument = new ScrapedOpinionDocument(opinionBase);
				parserDocument.setFootnotes( footnotes );
				parserDocument.setParagraphs( paragraphs );

				// not efficient, but it works for loading
				// if you are going to change it then watch for not complete 
				// 
				synchronized ( citationStore ) {
					ParsedOpinionCitationSet parserResults = parser.parseOpinionDocument(parserDocument, opinionBase, citationStore);
					citationStore.mergeParsedDocumentCitations(opinionBase, parserResults);
					citationStore.persistOpinion(opinionBase);
//					System.out.println( opinionSummary.fullPrint() );
				}
			}
		}
	}
}
