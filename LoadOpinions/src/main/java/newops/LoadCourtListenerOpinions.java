package newops;

import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.statute.api.IStatuteApi;
import com.github.karlnicholas.legalservices.statuteca.statuteapi.CAStatuteApiImpl;

import archivereader.ReadCourtListenerFiles;

public class LoadCourtListenerOpinions {
//	private static Logger logger = Logger.getLogger(LoadHistoricalOpinions.class.getName());
	public static void main(String[] args) throws Exception {

		CitationStore citationStore = CitationStore.getInstance();
    	IStatuteApi iStatuteApi = new CAStatuteApiImpl();
		iStatuteApi.loadStatutes();

		ReadCourtListenerFiles file1 = new ReadCourtListenerFiles(citationStore, iStatuteApi.getStatutesTitles());
	    file1.loadFiles("c:/users/karln/downloads/calctapp-opinions.tar.gz", "c:/users/karln/downloads/calctapp-clusters.tar.gz", 1000);
//
	    ReadCourtListenerFiles file2 = new ReadCourtListenerFiles(citationStore, iStatuteApi.getStatutesTitles());
	    file2.loadFiles("c:/users/karln/downloads/cal-opinions.tar.gz", "c:/users/karln/downloads/cal-clusters.tar.gz", 1000);

	    new WriteCitationStore().writeCitationStore(citationStore, iStatuteApi);

        System.out.println("LoadCourtListenerOpinions: DONE");
    }	     

}