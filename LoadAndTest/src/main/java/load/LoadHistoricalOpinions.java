package load;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import opca.memorydb.CitationStore;
import opca.model.OpinionBase;
import opca.model.OpinionStatuteCitation;
import opca.model.StatuteCitation;
import opca.repository.OpinionBaseRepository;
import opca.repository.OpinionStatuteCitationRepository;
import opca.repository.StatuteCitationRepository;
import statutes.api.IStatutesApi;
import statutesca.statutesapi.CAStatutesApiImpl;

@Service
public class LoadHistoricalOpinions {
//	private static Logger logger = Logger.getLogger(LoadHistoricalOpinions.class.getName());
	private final CitationStore citationStore;
	private final OpinionBaseRepository opinionBaseRepository;
	private final StatuteCitationRepository statuteCitationRepository;
	private final OpinionStatuteCitationRepository opinionStatuteCitationRepoistory;
//	OpinionDocumentParser parser;
	

	public LoadHistoricalOpinions(OpinionBaseRepository opinionBaseRepository,
			StatuteCitationRepository statuteCitationRepository,
			OpinionStatuteCitationRepository opinionStatuteCitationRepoistory) {
		this.citationStore = CitationStore.getInstance(); 
		this.opinionBaseRepository = opinionBaseRepository;
		this.statuteCitationRepository = statuteCitationRepository;
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
    		opinionBaseRepository.save(opinion);
    	}

    	for(StatuteCitation statute: citationStore.getAllStatutes() ) {
    		statuteCitationRepository.save(statute);
    	}
    	for(OpinionStatuteCitation opinionStatuteCitation: opinionStatuteCitations ) {
    		opinionStatuteCitationRepoistory.save(opinionStatuteCitation);
    	}
   	
    }

}