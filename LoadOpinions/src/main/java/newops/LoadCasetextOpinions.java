package newops;

import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.statute.api.IStatuteApi;
import com.github.karlnicholas.legalservices.statuteca.statuteapi.CAStatuteApiImpl;

import archivereader.ReadCasetextFiles;

public class LoadCasetextOpinions {

	public static void main(String[] args) throws Exception {

		CitationStore citationStore = CitationStore.getInstance();
    	IStatuteApi iStatuteApi = new CAStatuteApiImpl();
		iStatuteApi.loadStatutes();

		ReadCasetextFiles file1 = new ReadCasetextFiles(citationStore, iStatuteApi.getStatutesTitles());
		
		file1.loadFiles("c:/users/karln/downloads/casetext/calcases.zip", 1000);

		new WriteCitationStore().cleanCitations(citationStore, iStatuteApi);
		
        System.out.println("LoadJustiaOpinions: DONE");
	}
}
