package com.github.karlnicholas.legalservices.opinion.memorydb;

import java.util.Iterator;
import java.util.TreeSet;

import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionStatuteCitation;
import com.github.karlnicholas.legalservices.opinion.model.StatuteCitation;

public class CitationStore {
    
    private TreeSet<OpinionBase> opinionTable;
    private TreeSet<StatuteCitation> statuteTable;
    private TreeSet<OpinionBase> opinionCitationTable;
    
    public TreeSet<OpinionBase> getOpinionTable() {
        return opinionTable;
    }

    public TreeSet<StatuteCitation> getStatuteTable() {
        return statuteTable;
    }

    public void setStatuteTable(TreeSet<StatuteCitation> statuteTable) {
        this.statuteTable = statuteTable;
    }

	public TreeSet<OpinionBase> getOpinionCitationTable() {
		return opinionCitationTable;
	}

	public void setOpinionCitationTable(TreeSet<OpinionBase> opinionCitationTable) {
		this.opinionCitationTable = opinionCitationTable;
	}
    private CitationStore(){
        opinionTable = new TreeSet<OpinionBase>();
        opinionCitationTable = new TreeSet<OpinionBase>();
        statuteTable = new TreeSet<StatuteCitation>();
    }
    private static class SingletonHelper {
        private static final CitationStore INSTANCE = new CitationStore();
    }
    public static CitationStore getInstance(){
        return SingletonHelper.INSTANCE;
    }
    
	public void clearDB() {
        opinionTable.clear();
        opinionCitationTable.clear();
        statuteTable.clear();
	}
    public int getCount() {
        return getStatuteTable().size();
    }

//    public StatuteCitation findStatuteByCodeSection(String title, String sectionNumber) {
//        return statuteExists(new StatuteCitation(new StatuteKey(title, sectionNumber)));
//    }
//	public StatuteCitation findStatuteByStatute(StatuteCitation statuteCitation) {
//        StatuteCitation foundCitation = getStatuteTable().floor(statuteCitation);
//        if ( statuteCitation.equals(foundCitation)) return foundCitation;
//        return null;
//	}    
//
//	public OpinionBase findOpinionByOpinion(OpinionBase opinionBase) {
//		OpinionBase foundOpinion = getOpinionTable().floor(opinionBase);
//        if ( opinionBase.equals(foundOpinion)) return foundOpinion;
//		return null;
//	}


//	public void persistStatute(StatuteCitation statuteCitation) {
//		statuteTable.add(statuteCitation);
//	}

//	public void replaceStatute(StatuteCitation statuteCitation) {
//		statuteTable.remove(statuteCitation);
//		statuteTable.add(statuteCitation);
//	}

//	public void replaceOpinion(OpinionBase existingOpinion) {
//		opinionTable.remove(existingOpinion);
//		opinionTable.add(existingOpinion);
//	}

	public OpinionBase opinionExists(OpinionBase opinionBase) {
//        OpinionSummary tempOpinion = new OpinionSummary(opinionBase);
        if ( opinionTable.contains(opinionBase))
        	return opinionTable.floor(opinionBase);
        else return null;
	}

	public StatuteCitation statuteExists(StatuteCitation statute) {
//      OpinionSummary tempOpinion = new OpinionSummary(opinionBase);
      if ( statuteTable.contains(statute))
      	return statuteTable.floor(statute);
      else return null;
	}

	public OpinionBase opinionCitationExists(OpinionBase opinionBase) {
//      OpinionSummary tempOpinion = new OpinionSummary(opinionBase);
      if ( opinionCitationTable.contains(opinionBase))
      	return opinionCitationTable.floor(opinionBase);
      else return null;
	}

	public void persistOpinion(OpinionBase opinionBase) {
		opinionTable.add(opinionBase);
	}

//	public List<StatuteCitation> getStatutes(Collection<StatuteCitation> statuteCitations) {
//		List<StatuteCitation> list = new ArrayList<StatuteCitation>();
//		for (StatuteCitation statuteCitation: statuteCitations ) {
//			StatuteCitation statute = statuteExists(statuteCitation);
//			if ( statute != null ) list.add(statute);
//		}
//		return list;
//	}

//	public List<OpinionBase> getOpinions(Collection<OpinionBase> opinions) {
//		List<OpinionBase> list = new ArrayList<OpinionBase>();
//		for (OpinionBase opinion: opinions ) {
//			OpinionBase tempOpinion = opinionExists(opinion);
//			if ( tempOpinion != null ) list.add(tempOpinion);
//		}
//		return list;
//	}
    public void mergeParsedDocumentCitations(OpinionBase opinionBase) {
    	Iterator<OpinionBase> pOpinionIterator = opinionBase.getOpinionCitations().iterator();
    	while ( pOpinionIterator.hasNext() ) {
    		OpinionBase opinionCitation = pOpinionIterator.next(); 
        	// first look and see if the citation is a known "real" citation
    		OpinionBase existingOpinion = opinionExists(opinionCitation);
            if (  existingOpinion != null ) {
            	// add citations where they don't already exist.
            	existingOpinion.addReferringOpinion(opinionBase);
            } else {
        		// then
        		OpinionBase existingOpinionCitation = opinionCitationExists(opinionCitation);
                if (  existingOpinionCitation != null ) {
                	existingOpinionCitation.addReferringOpinion(opinionBase);
                } else {
                	opinionCitationTable.add(opinionCitation);
                }
            }
    	}
//TODO: WTF is all this about?    
    	for ( OpinionStatuteCitation opinionStatuteCitation: opinionBase.getStatuteCitations() ) {
			StatuteCitation existingStatuteCitation = statuteExists(opinionStatuteCitation.getStatuteCitation());
    		if ( existingStatuteCitation != null ) {
    			existingStatuteCitation.addOpinionCitation(opinionStatuteCitation);
    		} else {
    			statuteTable.add(opinionStatuteCitation.getStatuteCitation());
    		}
    	}
    }


}
