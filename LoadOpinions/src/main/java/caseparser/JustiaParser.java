package caseparser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.opinion.parser.OpinionDocumentParser;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;

import model.JustiaOpinion;

/**
 * Create new OpinionSummaries from LoadOpinion types, add to citationStore.
 * Merging?
 * 
 * @author karl
 *
 */
public class JustiaParser extends BaseParser implements Runnable {
	private final List<JustiaOpinion> clOps;

	public JustiaParser(List<JustiaOpinion> clOps, CitationStore citationStore, StatutesTitles[] statutesTitles) {
		super(citationStore, new OpinionDocumentParser(statutesTitles));
		this.clOps = clOps;
	}

	@Override
	public void run() {
//		for (LoadOpinion op : clOps) {
		int l = clOps.size();
		int i;
		for (i=0; i < l; ++i) {
			JustiaOpinion op = clOps.get(i);
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
			processOpinion(op, paragraphs, footnotes, citation);
		}
	}

}
