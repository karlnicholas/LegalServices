package opca.parser;

import java.util.*;

import statutes.StatutesTitles;
import opca.memorydb.CitationStore;
import opca.model.*;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 5/29/12
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 * 
 */
public class OpinionDocumentsParser extends OpinionDocumentParser {

    public OpinionDocumentsParser(StatutesTitles[] codeTitles) {
    	super(codeTitles);
    }

    /*
     * Should be thread safe
     */
    public ParsedOpinionCitationSet parseOpinionDocuments(
    	ScrapedOpinionDocument parserDocument, 
		OpinionBase opinionBase, 
		CitationStore citationStore
	) {
    	String defaultCodeSection;
    	ParsedOpinionCitationSet parserResults = new ParsedOpinionCitationSet();
        // this breaks it into sentences and paragraphs
    	// and stores them in singletons
//        readOpinion( inputStream, paragraphs, footnotes );
    	defaultCodeSection = analyzeDefaultCodes(opinionBase, parserDocument);

        // this analyzes sentences .. 
        TreeSet<StatuteCitation> codeCitationTree = new TreeSet<StatuteCitation>();
        TreeSet<OpinionBase> caseCitationTree = new TreeSet<OpinionBase>();

        parseDoc( 
    		opinionBase, 
        	parserDocument, 
    		codeCitationTree, 
    		caseCitationTree, 
    		defaultCodeSection
    	);
        // Check to see if two elements have the same section number but one was affirmative and the other was default
        // If so, then combine them and
        checkDesignatedCodeSections( codeCitationTree, opinionBase);

        collapseCodeSections( codeCitationTree, opinionBase);
        
        // What are we going to do with the citations?
        // need to use the StatuteFacade to add the to the external list
        List<StatuteCitation> statutes = new ArrayList<StatuteCitation>(codeCitationTree);
        Set<StatuteCitation> goodStatutes = new TreeSet<StatuteCitation>();
        for ( StatuteCitation statuteCitation: statutes) {
        	// forever get rid of statutes without a referenced code.
        	if( statuteCitation.getStatuteKey().getLawCode() != null ) {
/*        		
if ( !statuteCitation.toString().equals("pen:245") ) {
	continue;
}
*/
                StatuteCitation existingCitation = citationStore.findStatuteByStatute(statuteCitation);
                if ( existingCitation != null ) {
                	OpinionStatuteCitation osr = statuteCitation.getOpinionStatuteReference(opinionBase);
                	existingCitation.incRefCount(opinionBase, osr.getCountReferences());
                	statuteCitation = existingCitation;
                }

    			parserResults.putStatuteCitation(statuteCitation);
    			goodStatutes.add(statuteCitation);
        	}
        }
        opinionBase.addStatuteCitations(goodStatutes);
        //
        List<OpinionBase> opinions = new ArrayList<OpinionBase>(caseCitationTree);
        Set<OpinionBase> goodOpinions = new TreeSet<OpinionBase>();
//        ail3.getAndIncrement();
        for ( OpinionBase opinionReferredTo: opinions) {
        	// forever get rid of statutes without a referenced code.
        	OpinionBase existingOpinion = citationStore.findOpinionByOpinion(opinionReferredTo);
            if ( existingOpinion != null ) {
            	existingOpinion.addReferringOpinion(opinionBase);
            	opinionReferredTo = existingOpinion;
            }
        	
			parserResults.putOpinionBase(opinionReferredTo);
			goodOpinions.add(opinionReferredTo);
        }
        opinionBase.setOpinionCitations(goodOpinions);
//        parserResults.putOpinionBase(opinionBase);
        
        // Sort according to sectionReferenced
//        Collections.sort(sectionReferences);
        
        return parserResults;
    }

}
