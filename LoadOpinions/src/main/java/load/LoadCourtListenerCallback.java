package load;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.statute.api.IStatuteApi;

import loadmodel.LoadOpinion;

public class LoadCourtListenerCallback implements CourtListenerCallback {
	private final Logger logger;
	private final CitationStore citationStore;
	private final IStatuteApi iStatutesApi;
	private final int processors;
	private final List<Callable<Object>> tasks;
	private final ExecutorService es;
	

	public LoadCourtListenerCallback(CitationStore citationStore, IStatuteApi iStatutesApi) {
		this.citationStore = citationStore;
		this.iStatutesApi = iStatutesApi;
		logger = Logger.getLogger(LoadCourtListenerCallback.class.getName());
		processors = Runtime.getRuntime().availableProcessors();
		es = Executors.newFixedThreadPool(processors);
		tasks = new ArrayList<Callable<Object>>();
	}

	/* (non-Javadoc)
	 * @see load.CourtListenerCallback#callBack(java.util.List)
	 */
	@Override
	public void callBack(List<LoadOpinion> clOps) {
		tasks.add(Executors.callable(new BuildCitationStore(clOps, citationStore, iStatutesApi)));
		if ( tasks.size() >= processors ) {
			try {
				 List<Future<Object>> results = es.invokeAll(tasks);
				 results.forEach(f->{
					 if (!f.isDone()) {
						 System.out.println("f not done");
					 }
					 if (f.isCancelled()) {
						 System.out.println("f cancelled");
					 }
				 });
				 if( results.size() != processors) {
					 System.out.println("short results");
				 }
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, "Callback tasks interruted", e);
			} finally {
				tasks.clear();
			}
		}
//		new BuildCitationStore(clOps, citationStore, parserInterface).run();
	}

	@Override
	public void shutdown() {
		try {
			es.invokeAll(tasks);
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Callback tasks interruted", e);
		} finally {
			tasks.clear();
		}
		es.shutdown();
		
	}
}