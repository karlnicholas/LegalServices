package load;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import opca.dao.OpinionBaseDao;
import opca.dao.OpinionStatuteCitationDao;
import opca.dao.StatuteCitationDao;
import opca.memorydb.CitationStore;
import opca.model.OpinionBase;
import opca.model.OpinionStatuteCitation;
import opca.model.StatuteCitation;
import statutes.api.IStatutesApi;
import statutesca.statutesapi.CAStatutesApiImpl;

@Service
public class LoadHistoricalOpinions {
//	private static Logger logger = Logger.getLogger(LoadHistoricalOpinions.class.getName());
	private final CitationStore citationStore;
	private final OpinionBaseDao opinionBaseDao;
	private final StatuteCitationDao statuteCitationDao;
	private final OpinionStatuteCitationDao opinionStatuteCitationRepoistory;
//	OpinionDocumentParser parser;
	

	public LoadHistoricalOpinions(OpinionBaseDao opinionBaseDao,
			StatuteCitationDao statuteCitationDao,
			OpinionStatuteCitationDao opinionStatuteCitationRepoistory) {
		this.citationStore = CitationStore.getInstance(); 
		this.opinionBaseDao = opinionBaseDao;
		this.statuteCitationDao = statuteCitationDao;
		this.opinionStatuteCitationRepoistory = opinionStatuteCitationRepoistory;
	}


	@Transactional
    public void initializeDB() throws Exception {
    	//
	    IStatutesApi iStatutesApi = new CAStatutesApiImpl();
	    iStatutesApi.loadStatutes();

	    LoadCourtListenerCallback cb1 = new LoadCourtListenerCallback(citationStore, iStatutesApi);
	    LoadCourtListenerFiles file1 = new LoadCourtListenerFiles(cb1);
	    file1.loadFiles("c:/users/karln/downloads/calctapp-opinions.tar.gz", "c:/users/karln/downloads/calctapp-clusters.tar.gz", 1000);

	    LoadCourtListenerCallback cb2 = new LoadCourtListenerCallback(citationStore, iStatutesApi);
	    LoadCourtListenerFiles file2 = new LoadCourtListenerFiles(cb2);
	    file2.loadFiles("c:/users/karln/downloads/cal-opinions.tar.gz", "c:/users/karln/downloads/cal-clusters.tar.gz", 1000);


		List<OpinionStatuteCitation> opinionStatuteCitations = new ArrayList<>();

		for(OpinionBase opinion: citationStore.getAllOpinions() ) {
    		if ( opinion.getStatuteCitations() != null ) {
	    		for ( OpinionStatuteCitation statuteCitation: opinion.getStatuteCitations() ) {
	    			opinionStatuteCitations.add(statuteCitation);
	    		}
    		}
    		opinionBaseDao.insert(opinion);
    	}

    	for(StatuteCitation statute: citationStore.getAllStatutes() ) {
    		statuteCitationDao.insert(statute);
    	}
    	for(OpinionStatuteCitation opinionStatuteCitation: opinionStatuteCitations ) {
    		opinionStatuteCitationRepoistory.insert(opinionStatuteCitation);
    	}
   	
    }

}