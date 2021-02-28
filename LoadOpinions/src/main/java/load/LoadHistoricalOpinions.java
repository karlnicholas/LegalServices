package load;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import opca.crud.OpinionBaseCrud;
import opca.crud.OpinionBaseOpinionCitationsCrud;
import opca.crud.OpinionStatuteCitationCrud;
import opca.crud.StatuteCitationCrud;
import opca.memorydb.CitationStore;
import opca.model.OpinionBase;
import opca.model.StatuteCitation;
import statutes.api.IStatutesApi;
import statutesca.statutesapi.CAStatutesApiImpl;

public class LoadHistoricalOpinions {
//	private static Logger logger = Logger.getLogger(LoadHistoricalOpinions.class.getName());
	private final CitationStore citationStore;
	private final OpinionBaseCrud opinionBaseCrud;
	private final OpinionBaseOpinionCitationsCrud opinionBaseOpinionCitationsCrud;
	private final StatuteCitationCrud statuteCitationCrud;
	private final OpinionStatuteCitationCrud opinionStatuteCitationCrud;

	public LoadHistoricalOpinions(
			OpinionBaseCrud opinionBaseCrud, 
    		OpinionBaseOpinionCitationsCrud opinionBaseOpinionCitationsCrud,
    		StatuteCitationCrud statuteCitationCrud,
    		OpinionStatuteCitationCrud opinionStatuteCitationCrud
	) {
		this.citationStore = CitationStore.getInstance(); 
		this.opinionBaseCrud = opinionBaseCrud; 
		this.opinionBaseOpinionCitationsCrud = opinionBaseOpinionCitationsCrud;
		this.statuteCitationCrud = statuteCitationCrud;
		this.opinionStatuteCitationCrud = opinionStatuteCitationCrud;
	}


    public void initializeDB(Connection con) throws Exception {
    	//
	    IStatutesApi iStatutesApi = new CAStatutesApiImpl();
	    iStatutesApi.loadStatutes();

	    LoadCourtListenerCallback cb1 = new LoadCourtListenerCallback(citationStore, iStatutesApi);
	    LoadCourtListenerFiles file1 = new LoadCourtListenerFiles(cb1);
	    file1.loadFiles("c:/users/karln/downloads/calctapp-opinions.tar.gz", "c:/users/karln/downloads/calctapp-clusters.tar.gz", 1000);

	    LoadCourtListenerCallback cb2 = new LoadCourtListenerCallback(citationStore, iStatutesApi);
	    LoadCourtListenerFiles file2 = new LoadCourtListenerFiles(cb2);
	    file2.loadFiles("c:/users/karln/downloads/cal-opinions.tar.gz", "c:/users/karln/downloads/cal-clusters.tar.gz", 1000);

	    System.out.println("O:" + citationStore.getAllOpinions().size());
	    System.out.println("S:" + citationStore.getAllStatutes().size());
	     
//	    for ( int i=0; i < file1.acs.size(); ++i) {
//	    	if ( file1.acs.get(i).ait.get() - file1.acs.get(i).ail.get() != 0 ) {
//	    	    System.out.println("file1:" + i + " : " + file1.acs.get(i).ait.get() + " : "+ file1.acs.get(i).ail.get() );
//	    	    System.out.println("file1: ail2-3 " + file1.acs.get(i).ail2.get() + " " + + file1.acs.get(i).ail3.get());
//	    	}
//	    	if ( file1.acs.get(i).loopEnd+1 !=  file1.acs.get(i).ait.get() ) {
//	    	    System.out.println("file1 le:" + file1.acs.get(i).loopEnd);
//	    	}
//	    }
//	    for ( int i=0; i < file2.acs.size(); ++i) {
//	    	if ( file2.acs.get(i).ait.get() - file2.acs.get(i).ail.get() != 0 ) {
//	    	    System.out.println("file2:" + i + " : " + file2.acs.get(i).ait.get() + " : "+ file2.acs.get(i).ail.get() );
//	    	    System.out.println("file2: ail2-3 " + file2.acs.get(i).ail2.get() + " " + + file2.acs.get(i).ail3.get());
//	    	}
//	    	if ( file2.acs.get(i).loopEnd+1 !=  file2.acs.get(i).ait.get() ) {
//	    	    System.out.println("file2 le:" + file2.acs.get(i).loopEnd);
//	    	}
//	    }
	    int BATCH_SIZE = 1000;
	    List<OpinionBase> opinionBatch = new ArrayList<>(BATCH_SIZE);
	    int i=0;
	    for ( OpinionBase opinionBase: citationStore.getAllOpinions()) {
	    	opinionBatch.add(opinionBase);
	    	if ( ++i % BATCH_SIZE == 0 ) {
				opinionBaseCrud.insertBatch(opinionBatch, con);
				opinionBatch.clear();
	    	}
	    }
	    if ( opinionBatch.size() > 0 ) {
			opinionBaseCrud.insertBatch(opinionBatch, con);
			opinionBatch.clear();
	    }

	    List<StatuteCitation> statuteBatch = new ArrayList<>(BATCH_SIZE);
	    i = 0;
		for(StatuteCitation statute: citationStore.getAllStatutes() ) {
    		statuteBatch.add(statute);
	    	if ( ++i % BATCH_SIZE == 0 ) {
	    		statuteCitationCrud.insertBatch(statuteBatch, con);
	    		statuteBatch.clear();
	    	}
    	}
	    if ( statuteBatch.size() > 0 ) {
	    	statuteCitationCrud.insertBatch(statuteBatch, con);
	    	statuteBatch.clear();
	    }
		
		for(OpinionBase opinion: citationStore.getAllOpinions() ) {
			opinionBaseOpinionCitationsCrud.insertBatch(opinion, con);
		}

		for(OpinionBase opinion: citationStore.getAllOpinions() ) {
			opinionStatuteCitationCrud.insertBatch(opinion, con);
		}
   	
    }

}