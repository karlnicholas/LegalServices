package newops;

import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.statute.api.IStatuteApi;
import com.github.karlnicholas.legalservices.statuteca.statuteapi.CAStatuteApiImpl;

import archivereader.ReadCasetextFiles;
import archivereader.ReadJustiaFiles;

public class LoadAllOpinions {

	public static void main(String[] args) throws Exception {

		CitationStore citationStore = CitationStore.getInstance();
    	IStatuteApi iStatuteApi = new CAStatuteApiImpl();
		iStatuteApi.loadStatutes();

//		ReadJustiaFiles fileJ = new ReadJustiaFiles(citationStore, iStatuteApi.getStatutesTitles());
//		fileJ.loadFiles("c:/users/karln/downloads/justia/casesCal.2d.zip", 1000);
//		fileJ.loadFiles("c:/users/karln/downloads/justia/casesCal.3d.zip", 1000);
//		fileJ.loadFiles("c:/users/karln/downloads/justia/casesCal.4th.zip", 1000);
//		file1.loadFiles("c:/users/karln/downloads/justia/casesCal.App.2d.zip", 1000);
//		file1.loadFiles("c:/users/karln/downloads/justia/casesCal.App.3d.zip", 1000);
//		file1.loadFiles("c:/users/karln/downloads/justia/casesCal.App.4th.zip", 1000);
		System.out.println("O:" + citationStore.getOpinionTable().size());
		System.out.println("OC:" + citationStore.getOpinionCitationTable().size());
		System.out.println("S:" + citationStore.getStatuteTable().size());
		System.out.println("SR:" + citationStore.getStatuteTable().stream().mapToInt(oc->oc.getReferringOpinions().size()).sum());
		System.out.println("SC:" + citationStore.getOpinionTable().stream().mapToInt(ob->ob.getStatuteCitations().size()).sum());

		ReadCasetextFiles file1 = new ReadCasetextFiles(citationStore, iStatuteApi.getStatutesTitles());
		file1.loadFiles("c:/users/karln/downloads/casetext/calcases.zip", 1000);

		new WriteCitationStore().cleanCitations(citationStore, iStatuteApi);
		
        System.out.println("LoadJustiaOpinions: DONE");
	}
}
