package caseparser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;
import com.github.karlnicholas.legalservices.opinion.parser.OpinionDocumentParser;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;

import model.CourtListenerCluster;
import model.CourtListenerOpinion;

/**
 * Create new OpinionSummaries from LoadOpinion types, add to citationStore.
 * Merging?
 * 
 * @author karl
 *
 */
public class CourtListenerParser extends BaseParser implements Runnable {
	private final List<CourtListenerCluster> clOps;

	public CourtListenerParser(List<CourtListenerCluster> clOps, CitationStore citationStore, StatutesTitles[] statutesTitles) {
		super(citationStore, new OpinionDocumentParser(statutesTitles));
		this.clOps = clOps;
	}

	@Override
	public void run() {
		int l = clOps.size();
		int i;
		for (i=0; i < l; ++i) {
			CourtListenerCluster courtListenerCluster = clOps.get(i);
			String citation = null;
			for ( CourtListenerCluster.Citation citeClass: courtListenerCluster.citations) {
				String cec = citeClass.volume + " " + citeClass.reporter.replace(" " , "") + " " + citeClass.page;
				if ( OpinionKey.testValidOpinionKey(cec) ) {
					citation = cec;
					break;
				}
			}
			
//			Document lawBox = Parser.parse(op.getHtml_lawbox(), "");
//			Elements ps = lawBox.getElementsByTag("p");
			List<String> paragraphs = new ArrayList<String>();
			List<String> footnotes = new ArrayList<String>();
			for ( CourtListenerOpinion opinion: courtListenerCluster.opinions) {
				if ( opinion.html_columbia != null && !opinion.html_columbia.isEmpty() ) {
					pParser(Jsoup.parse(opinion.html_columbia).getElementsByTag("p"), paragraphs, footnotes);
				} else if ( opinion.html_lawbox != null && !opinion.html_lawbox.isEmpty() ) {
					pParser(Jsoup.parse(opinion.html_lawbox).getElementsByTag("p"), paragraphs, footnotes);
				}
			}
//
			// if ( name != null && name.contains("Rptr.") ) name =
			// op.getCitation();
			processOpinion(courtListenerCluster, paragraphs, footnotes, citation);
		}
	}

	private void pParser(Elements ps, List<String> paragraphs, List<String> footnotes) {
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
	}
}
