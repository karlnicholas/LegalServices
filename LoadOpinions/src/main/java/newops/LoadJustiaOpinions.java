package newops;

import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.statute.api.IStatuteApi;
import com.github.karlnicholas.legalservices.statuteca.statuteapi.CAStatuteApiImpl;

import archivereader.ReadJustiaFiles;

public class LoadJustiaOpinions {

	public static void main(String[] args) throws Exception {

		CitationStore citationStore = CitationStore.getInstance();
    	IStatuteApi iStatuteApi = new CAStatuteApiImpl();
		iStatuteApi.loadStatutes();
		ReadJustiaFiles file1 = new ReadJustiaFiles(citationStore, iStatuteApi.getStatutesTitles());
		
		file1.loadFiles("c:/users/karln/downloads/justia/casesCal.2d.zip", 1000);
		file1.loadFiles("c:/users/karln/downloads/justia/casesCal.3d.zip", 1000);
		file1.loadFiles("c:/users/karln/downloads/justia/casesCal.4th.zip", 1000);
//		file1.loadFiles("c:/users/karln/downloads/justia/casesCal.App.2d.zip", 1000);
//		file1.loadFiles("c:/users/karln/downloads/justia/casesCal.App.3d.zip", 1000);
//		file1.loadFiles("c:/users/karln/downloads/justia/casesCal.App.4th.zip", 1000);

		new WriteCitationStore().cleanCitations(citationStore, iStatuteApi);
		
        System.out.println("LoadJustiaOpinions: DONE");
	}
}
