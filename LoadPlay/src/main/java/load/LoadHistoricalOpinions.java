package load;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import opca.dao.OpinionBaseDao;
import opca.dao.OpinionBaseOpinionCitationsDao;
import opca.dao.OpinionStatuteCitationDao;
import opca.dao.StatuteCitationDao;
import opca.memorydb.CitationStore;
import statutes.api.IStatutesApi;
import statutesca.statutesapi.CAStatutesApiImpl;

@Service
public class LoadHistoricalOpinions {
////	private static Logger logger = Logger.getLogger(LoadHistoricalOpinions.class.getName());
	private final CitationStore citationStore;
	private final AtomicInteger ai = new AtomicInteger();
//	private final OpinionBaseDao opinionBaseDao;
//	private final StatuteCitationDao statuteCitationDao;
//	private final OpinionStatuteCitationDao opinionStatuteCitationDao;
//	private final OpinionBaseOpinionCitationsDao opinionBaseOpinionCitationsDao;
////	OpinionDocumentParser parser;
//	
//
//	public LoadHistoricalOpinions(OpinionBaseDao opinionBaseDao,
//			StatuteCitationDao statuteCitationDao,
//			OpinionStatuteCitationDao opinionStatuteCitationDao, 
//			OpinionBaseOpinionCitationsDao opinionBaseOpinionCitationsDao) {
//		this.citationStore = CitationStore.getInstance(); 
//		this.opinionBaseDao = opinionBaseDao;
//		this.statuteCitationDao = statuteCitationDao;
//		this.opinionStatuteCitationDao = opinionStatuteCitationDao;
//		this.opinionBaseOpinionCitationsDao = opinionBaseOpinionCitationsDao;
//	}
//
//	public LoadHistoricalOpinions() {
//		this.citationStore = CitationStore.getInstance(); 
//		this.opinionBaseDao = null;
//		this.statuteCitationDao = null;
//		this.opinionStatuteCitationDao = null;
//		this.opinionBaseOpinionCitationsDao = null;
//	}

	public LoadHistoricalOpinions() {
		this.citationStore = CitationStore.getInstance(); 
	}

	@Transactional
    public void initializeDB() throws Exception {
    	//
	    IStatutesApi iStatutesApi = new CAStatutesApiImpl();
	    iStatutesApi.loadStatutes();
	    

	    LoadCourtListenerCallback cb1 = new LoadCourtListenerCallback(citationStore, iStatutesApi, ai);
	    LoadCourtListenerFiles file1 = new LoadCourtListenerFiles(cb1);
	    file1.loadFiles("c:/users/karln/downloads/calctapp-opinions.tar.gz", "c:/users/karln/downloads/calctapp-clusters.tar.gz", 1000);

	    LoadCourtListenerCallback cb2 = new LoadCourtListenerCallback(citationStore, iStatutesApi, ai);
	    LoadCourtListenerFiles file2 = new LoadCourtListenerFiles(cb2);
	    file2.loadFiles("c:/users/karln/downloads/cal-opinions.tar.gz", "c:/users/karln/downloads/cal-clusters.tar.gz", 1000);

	    System.out.println("O:" + citationStore.getAllOpinions().size());
	    System.out.println("S:" + citationStore.getAllStatutes().size());
	    System.out.println("ai:" + ai.get());
	    
	    
	    
//		for(OpinionBase opinion: citationStore.getAllOpinions() ) {
//    		opinionBaseDao.insert(opinion);
//    	}
//
//		for(OpinionBase opinion: citationStore.getAllOpinions() ) {
//			opinionBaseOpinionCitationsDao.insert(opinion);
//		}
//		System.out.println(OpinionBaseOpinionCitationsDao.good.get()+":"+OpinionBaseOpinionCitationsDao.bad.get());
//
//		for(StatuteCitation statute: citationStore.getAllStatutes() ) {
//    		statuteCitationDao.insert(statute);
//    	}
//		
//		for(OpinionBase opinion: citationStore.getAllOpinions() ) {
//			opinionStatuteCitationDao.insert(opinion);
//		}
//		System.out.println(OpinionStatuteCitationDao.good.get()+":"+OpinionStatuteCitationDao.bad.get());
//   	
    }

}