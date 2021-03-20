package newops;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.github.karlnicholas.legalservices.opinion.memorydb.CitationStore;
import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.statute.api.IStatuteApi;
import com.github.karlnicholas.legalservices.statuteca.statuteapi.CAStatuteApiImpl;

import loadnew.LoadCourtListenerCallback;
import loadnew.LoadCourtListenerFiles;

public class LoadNewOpinions {

	public static void main(String[] args) throws Exception {
		CitationStore citationStore = CitationStore.getInstance(); 
	    IStatuteApi iStatutesApi = new CAStatuteApiImpl();
	    iStatutesApi.loadStatutes();

	    LoadCourtListenerCallback cb1 = new LoadCourtListenerCallback(citationStore, iStatutesApi);
	    LoadCourtListenerFiles file1 = new LoadCourtListenerFiles(cb1);

//	    file1.loadFiles("c:/users/karln/downloads/justia/casesCal.2d.zip", 1000);
//	    file1.loadFiles("c:/users/karln/downloads/justia/casesCal.3d.zip", 1000);
	    file1.loadFiles("c:/users/karln/downloads/justia/casesCal.4th.zip", 1000);
//	    file1.loadFiles("c:/users/karln/downloads/justia/casesCal.App.2d.zip", 1000);
//	    file1.loadFiles("c:/users/karln/downloads/justia/casesCal.App.3d.zip", 1000);
//	    file1.loadFiles("c:/users/karln/downloads/justia/casesCal.App.4th.zip", 1000);
		
	    System.out.println("O:" + citationStore.getOpinionTable().size());
	    System.out.println("OC:" + citationStore.getOpinionCitationTable().size());
	    System.out.println("S:" + citationStore.getStatuteTable().size());

		for ( OpinionBase o: citationStore.getOpinionTable() ) {
			if ( o.getOpinionKey().toString().equals("79 Cal.App.4th 719")) {
				System.out.println(o);
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

	    
	    System.out.println("O:" + citationStore.getOpinionTable().size());
	    System.out.println("OC:" + citationStore.getOpinionCitationTable().size());
	    System.out.println("S:" + citationStore.getStatuteTable().size());
	    
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
	     
		Set<OpinionBase> goodReferences = new TreeSet<>();
		for ( OpinionBase o: citationStore.getOpinionTable() ) {
			if ( o.getReferringOpinions() != null )
				goodReferences.addAll(o.getReferringOpinions());
	    }
		System.out.println("Good referringOpinion count: " + goodReferences.size());

		Set<OpinionBase> badReferences = new TreeSet<>();
		for ( OpinionBase o: citationStore.getOpinionCitationTable() ) {
    		if ( Math.random() > .999 ) {
    			System.out.println(o.getOpinionKey() + " : " + o.getReferringOpinions());
    		}
    		badReferences.addAll(o.getReferringOpinions());
	    }
		System.out.println("Bad referringOpinion count: " + badReferences.size());

		Set<OpinionBase> totalCitations = new TreeSet<>();
		for ( OpinionBase o: citationStore.getOpinionTable() ) {
			if ( o.getOpinionCitations() != null ) {
				totalCitations.addAll(o.getOpinionCitations());
			}
	    }
		System.out.println("Unique Citations count: " + totalCitations.size());

		totalCitations.clear();
		for ( OpinionBase o: citationStore.getOpinionTable() ) {
			if ( o.getOpinionCitations() != null ) {
				totalCitations.addAll(o.getOpinionCitations());
			}
	    }
		System.out.println("Unique Citations count: " + totalCitations.size());

		badReferences.clear();
		for ( OpinionBase o: citationStore.getOpinionCitationTable() ) {
    		if ( o.getReferringOpinions().size() > 0 ) {
    			System.out.println(o.getOpinionKey() + " : " + o.getReferringOpinions());
    			System.out.println("            :" + o.getReferringOpinions().iterator().next().getOpinionCitations());
    		}
    		badReferences.addAll(o.getReferringOpinions());
	    }
		System.out.println("Bad referringOpinion count: " + badReferences.size());

	}
	
	public void loadOpinions() throws Exception {
		Pattern pattern = Pattern.compile("[\\(\\)]");
		ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get("c:/users/karln/downloads/justia/casesCal.2d.zip")));
		ZipEntry zipEntry = zipInputStream.getNextEntry();
		while ( zipEntry != null ) {
			Document d = Jsoup.parse(new String(zipInputStream.readAllBytes()));
			Element o = d.selectFirst("div#opinion");
			System.out.print( " " + o.select("p").size());
//			List<String> paragraphs = o.select("p").stream().map(Element::text).collect(Collectors.toList());
//			System.out.print( " " + o.select("a.related-case").size());
//			System.out.println(d.getAllElements().size());
			zipInputStream.closeEntry();
			System.out.println( " " + Arrays.asList(pattern.split(zipEntry.getName())));
			zipEntry = zipInputStream.getNextEntry();
		}
		
	}

}
