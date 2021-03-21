package loadnew;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.github.karlnicholas.legalservices.opinion.crud.OpinionBaseCrud;
import com.github.karlnicholas.legalservices.opinion.crud.OpinionBaseOpinionCitationsCrud;
import com.github.karlnicholas.legalservices.opinion.crud.OpinionStatuteCitationCrud;
import com.github.karlnicholas.legalservices.opinion.crud.StatuteCitationCrud;
import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionStatuteCitation;
import com.github.karlnicholas.legalservices.opinion.model.StatuteCitation;
import com.github.karlnicholas.legalservices.statute.SectionNumber;
import com.github.karlnicholas.legalservices.statute.StatutesBaseClass;
import com.github.karlnicholas.legalservices.statute.api.IStatuteApi;
import com.github.karlnicholas.legalservices.statuteca.statuteapi.CAStatuteApiImpl;

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

    	IStatuteApi iStatuteApi = new CAStatuteApiImpl();
		iStatuteApi.loadStatutes();
		LoadCourtListenerCallback cb1 = new LoadCourtListenerCallback(citationStore, iStatuteApi.getStatutesTitles());
		LoadCourtListenerFiles file1 = new LoadCourtListenerFiles(cb1);
		
		file1.loadFiles("c:/users/karln/downloads/justia/casesCal.2d.zip", 1000);
		file1.loadFiles("c:/users/karln/downloads/justia/casesCal.3d.zip", 1000);
		file1.loadFiles("c:/users/karln/downloads/justia/casesCal.4th.zip", 1000);
		file1.loadFiles("c:/users/karln/downloads/justia/casesCal.App.2d.zip", 1000);
		file1.loadFiles("c:/users/karln/downloads/justia/casesCal.App.3d.zip", 1000);
		file1.loadFiles("c:/users/karln/downloads/justia/casesCal.App.4th.zip", 1000);
		
		System.out.println("O:" + citationStore.getOpinionTable().size());
		System.out.println("OC:" + citationStore.getOpinionCitationTable().size());
		System.out.println("S:" + citationStore.getStatuteTable().size());
		System.out.println("SR:" + citationStore.getStatuteTable().stream().mapToInt(oc->oc.getReferringOpinions().size()).sum());
		
		Set<OpinionBase> goodReferences = new TreeSet<>();
		for ( OpinionBase o: citationStore.getOpinionTable() ) {
			if ( o.getReferringOpinions() != null )
				goodReferences.addAll(o.getReferringOpinions());
		}
		System.out.println("Unique referringOpinions count: " + goodReferences.size());
		
		Set<OpinionBase> totalCitations = new TreeSet<>();
		for ( OpinionBase o: citationStore.getOpinionTable() ) {
			if ( o.getOpinionCitations() != null ) {
				totalCitations.addAll(o.getOpinionCitations());
			}
		}
		System.out.println("Unique Citations count        : " + totalCitations.size());
		
		
		Iterator<StatuteCitation> pStatuteIterator = citationStore.getStatuteTable().iterator();
		while ( pStatuteIterator.hasNext() ) {
			StatuteCitation statuteCitation = pStatuteIterator.next();
			StatutesBaseClass statuteRef = iStatuteApi.findReference(statuteCitation.getStatuteKey().getLawCode(), new SectionNumber(-1, statuteCitation.getStatuteKey().getSectionNumber()));
			if ( statuteRef == null ) {
				Iterator<OpinionStatuteCitation> scIter = statuteCitation.getReferringOpinions().iterator();
				while ( scIter.hasNext() ) {
					OpinionStatuteCitation osc = scIter.next();
					osc.getOpinionBase().removeOpinionStatuteCitation(osc);
					scIter.remove();
				}
				pStatuteIterator.remove();
			}
		}

		Iterator<OpinionBase> pOpinionIterator = citationStore.getOpinionCitationTable().iterator();
		while ( pOpinionIterator.hasNext() ) {
			OpinionBase opinionCitation = pOpinionIterator.next(); 
			// first look and see if the citation is a known "real" citation
			OpinionBase existingOpinion = citationStore.opinionExists(opinionCitation);
		    if (  existingOpinion != null ) {
		    	// add citations where they don't already exist.
		    	pOpinionIterator.remove();
		    	existingOpinion.addAllReferringOpinions(opinionCitation.getReferringOpinions());
		    }
		}
		
		for ( OpinionBase o: citationStore.getOpinionTable() ) {
			if ( o.getOpinionCitations() != null ) {
				Iterator<OpinionBase> ocIt = o.getOpinionCitations().iterator();
				while ( ocIt.hasNext() ) {
					OpinionBase oc = ocIt.next();
					OpinionBase boc = citationStore.opinionCitationExists(oc);
					if ( boc != null ) {
						ocIt.remove();
						boc.getReferringOpinions().remove(o);
					}
				}
			}
		}
		
		List<OpinionBase> opinionCitations = new ArrayList<>();
		for ( OpinionBase o: citationStore.getOpinionTable() ) {
			if ( o.getOpinionCitations() != null ) {
				opinionCitations.clear();
				Iterator<OpinionBase> ocIt = o.getOpinionCitations().iterator();
				while ( ocIt.hasNext() ) {
					OpinionBase oc = ocIt.next();
					OpinionBase boc = citationStore.opinionExists(oc);
					if ( boc == null ) {
						System.out.println("Oh shit");
					} else {
						ocIt.remove();
						opinionCitations.add(boc);
					}
				}
				o.getOpinionCitations().addAll(opinionCitations);
			}
			if ( o.getStatuteCitations() != null ) {
				Iterator<OpinionStatuteCitation> oscIt = o.getStatuteCitations().iterator();
				while ( oscIt.hasNext() ) {
					OpinionStatuteCitation osc = oscIt.next();
					StatuteCitation bsc = citationStore.statuteExists(osc.getStatuteCitation());
					if ( bsc == null ) {
						System.out.println("Oh shit");
					} else {
						osc.setStatuteCitation(bsc);
					}
				}
			}
		}

		Iterator<OpinionBase> obIt = citationStore.getOpinionTable().iterator();
		while (obIt.hasNext()) {
			OpinionBase o = obIt.next();
			if ( (o.getOpinionCitations() == null || o.getOpinionCitations().size() == 0)
					&& (o.getStatuteCitations() == null || o.getStatuteCitations().size() == 0)
					&& (o.getReferringOpinions()== null || o.getReferringOpinions().size() == 0)
			) {
				obIt.remove();
			}
		}

		System.out.println("\rO:" + citationStore.getOpinionTable().size());
		System.out.println("OC:" + citationStore.getOpinionCitationTable().size());
		System.out.println("S:" + citationStore.getStatuteTable().size());
		System.out.println("SR:" + citationStore.getStatuteTable().stream().mapToInt(oc->oc.getReferringOpinions().size()).sum());
		
		try ( BufferedWriter bw = Files.newBufferedWriter(Paths.get("c:/users/karln/downloads/opcitations.txt"), StandardOpenOption.CREATE)) {
			citationStore.getOpinionTable().forEach(op->{
				try {
					bw.write(op.getOpinionKey().toString());
					bw.newLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			});
		}
		 
		goodReferences.clear();
		for ( OpinionBase o: citationStore.getOpinionTable() ) {
			if ( o.getReferringOpinions() != null )
				goodReferences.addAll(o.getReferringOpinions());
		}
		System.out.println("Unique referringOpinions count: " + goodReferences.size());
		
		totalCitations.clear();
		for ( OpinionBase o: citationStore.getOpinionTable() ) {
			if ( o.getOpinionCitations() != null ) {
				totalCitations.addAll(o.getOpinionCitations());
			}
		}
		System.out.println("Unique Citations count        : " + totalCitations.size());
		
		Set<OpinionBase> badCitations = new TreeSet<>();
		for ( OpinionBase o: citationStore.getOpinionCitationTable() ) {
			if ( o.getReferringOpinions() != null ) {
				badCitations.addAll(o.getReferringOpinions());
			}
		}
		System.out.println("Bad Citations count: " + badCitations.size());

	    int BATCH_SIZE = 1000;
	    List<OpinionBase> opinionBatch = new ArrayList<>(BATCH_SIZE);
	    int i=0;
	    for ( OpinionBase opinionBase: citationStore.getOpinionTable()) {
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
		for(StatuteCitation statute: citationStore.getStatuteTable() ) {
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
		
		for(OpinionBase opinion: citationStore.getOpinionTable() ) {
			opinionBaseOpinionCitationsCrud.insertBatch(opinion, con);
		}

		for(OpinionBase opinion: citationStore.getOpinionTable() ) {
			opinionStatuteCitationCrud.insertBatch(opinion, con);
		}
   	
    }
}