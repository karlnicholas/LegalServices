package loadnew;

import java.util.List;

import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;

import loadmodelnew.LoadOpinionNew;

public class LoadCourtListenerCallback implements CourtListenerCallback {
	private final CitationStore citationStore;
	private final StatutesTitles[] statutesTitles;
	

	public LoadCourtListenerCallback(CitationStore citationStore, StatutesTitles[] statutesTitles) {
		this.citationStore = citationStore;
		this.statutesTitles = statutesTitles;
	}

	/* (non-Javadoc)
	 * @see load.CourtListenerCallback#callBack(java.util.List)
	 */
	@Override
	public void callBack(List<LoadOpinionNew> clOps) {
		new BuildCitationStore(clOps, citationStore, statutesTitles).run();
	}

	@Override
	public void shutdown() {
	}
}