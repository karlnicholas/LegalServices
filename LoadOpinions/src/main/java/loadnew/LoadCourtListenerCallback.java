package loadnew;

import java.util.List;

import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.statute.api.IStatuteApi;

import loadmodelnew.LoadOpinionNew;

public class LoadCourtListenerCallback implements CourtListenerCallback {
	private final CitationStore citationStore;
	private final IStatuteApi iStatutesApi;
	

	public LoadCourtListenerCallback(CitationStore citationStore, IStatuteApi iStatutesApi) {
		this.citationStore = citationStore;
		this.iStatutesApi = iStatutesApi;
	}

	/* (non-Javadoc)
	 * @see load.CourtListenerCallback#callBack(java.util.List)
	 */
	@Override
	public void callBack(List<LoadOpinionNew> clOps) {
		new BuildCitationStore(clOps, citationStore, iStatutesApi).run();
	}

	@Override
	public void shutdown() {
	}
}