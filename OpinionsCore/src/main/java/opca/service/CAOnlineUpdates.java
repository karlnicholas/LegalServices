package opca.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import opca.dao.OpinionBaseDao;
import opca.dao.OpinionStatuteCitationDao;
import opca.dao.SlipOpinionDao;
import opca.dao.SlipPropertiesDao;
import opca.dao.StatuteCitationDao;
import opca.memorydb.CitationStore;
import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.OpinionStatuteCitation;
import opca.model.SlipOpinion;
import opca.model.SlipProperties;
import opca.model.StatuteCitation;
import opca.model.StatuteKey;
import opca.parser.OpinionScraperInterface;
import opca.parser.OpinionDocumentParser;
import opca.parser.ScrapedOpinionDocument;
import opca.parser.ParsedOpinionCitationSet;
import statutes.StatutesTitles;
import statutes.service.StatutesService;

/**
 * 
 * @author karl
 *
 */
@Service
public class CAOnlineUpdates {	
	Logger logger = LoggerFactory.getLogger(CAOnlineUpdates.class);
	private final OpinionBaseDao opinionBaseDao;
	private final StatuteCitationDao statuteCitationDao;
	private final OpinionStatuteCitationDao opinionStatuteCitationRepoistory;
	private final SlipOpinionDao slipOpinionDao;
	private final SlipPropertiesDao slipPropertiesDao;
	
	public CAOnlineUpdates(OpinionBaseDao opinionBaseDao,
			StatuteCitationDao statuteCitationDao,
			OpinionStatuteCitationDao opinionStatuteCitationRepoistory,
			SlipOpinionDao slipOpinionDao, SlipPropertiesDao slipPropertiesDao) {
		this.opinionBaseDao = opinionBaseDao;
		this.statuteCitationDao = statuteCitationDao;
		this.opinionStatuteCitationRepoistory = opinionStatuteCitationRepoistory;
		this.slipOpinionDao = slipOpinionDao;
		this.slipPropertiesDao = slipPropertiesDao;
	}

//	public void updateOpinionViews(List<OpinionKey> opinionKeys, BlockingStatutesService blockingStatutesService) {
//        opinionViewSingleton.updateOpinionViews(opinionKeys, blockingStatutesService);
//	}

	// @Transactional very important, won't work without it.
	public List<OpinionKey> updateDatabase(OpinionScraperInterface caseScraper, StatutesService statutesService) {
		
 		List<SlipOpinion> onlineOpinions = caseScraper.getCaseList();
 		// save OpinionKeys for cache handling 
		List<OpinionKey> opinionKeys = new ArrayList<>();
		for( SlipOpinion slipOpinion: onlineOpinions) {
			opinionKeys.add(slipOpinion.getOpinionKey());
		}
		if ( onlineOpinions == null || onlineOpinions.size() == 0 ) {
			logger.info("No cases found online: returning.");
//				return opinionKeys;
		}
		//
//			onlineOpinions.remove(0);
//			onlineOpinions = onlineOpinions.subList(0, 340);
//			onlineOpinions = onlineOpinions.subList(0, 0);
//			onlineOpinions = onlineOpinions.subList(0, 1);
//		onlineOpinions = onlineOpinions.subList(0,1);

//
//		Iterator<SlipOpinion> oit = onlineOpinions.iterator();
//		while ( oit.hasNext() ) {
//			SlipOpinion opinion = oit.next();
//			if ( 
//				!opinion.getFileName().equalsIgnoreCase("B299987")
////					|| opinion.getFileName().equalsIgnoreCase("C080488")
////					|| opinion.getFileName().equalsIgnoreCase("E070545M")
////					|| opinion.getFileName().equalsIgnoreCase("C082144")
////					|| opinion.getFileName().equalsIgnoreCase("C080023")
////					|| opinion.getFileName().equalsIgnoreCase("B286043")
////					|| opinion.getFileName().equalsIgnoreCase("S087773")
////					|| opinion.getFileName().equalsIgnoreCase("JAD18-11")
//			) {
//				oit.remove();
//			}
//		}
//			
		//
		List<SlipOpinion> currentOpinions = slipOpinionDao.findAll();
		List<SlipOpinion> currentCopy = new ArrayList<SlipOpinion>(currentOpinions);
		logger.info("Found " + currentCopy.size() + " in the database.");
		logger.info("Split Transactions" );		
		// Determine old cases
		// remove online cases from dbCopy
		// what's left is no longer in online List
		Iterator<SlipOpinion> dbit = currentCopy.iterator();
		while ( dbit.hasNext() ) {
			SlipOpinion currentOpinion = dbit.next();
			if ( onlineOpinions.contains(currentOpinion) ) {
				dbit.remove();
			}
		}

		if( currentCopy.size() > 0 ) {
			deleteExistingOpinions(currentCopy);
		} else {
			logger.info("No cases deleted.");
		}

		// Determine new cases
		// remove already persisted cases from onlineList
		for ( SlipOpinion dbCase: currentOpinions ) {
			int idx = onlineOpinions.indexOf(dbCase);
			if ( idx >= 0 ) {
				onlineOpinions.remove(idx);
			}
		}
		if ( onlineOpinions.size() > 0 ) {
			// no retries
			processAndPersistCases(onlineOpinions, caseScraper, statutesService);
		} else {
			logger.info("No new cases.");
		}		
//			processAndPersistCases(onlineOpinions, caseScraper);
		return opinionKeys; 
	}
	
	private void processAndPersistCases(List<SlipOpinion> slipOpinions, OpinionScraperInterface opinionScraper, StatutesService statutesService) {

		// Create the CACodes list
		logger.info("There are " + slipOpinions.size() + " SlipOpinions to process");
		List<ScrapedOpinionDocument> scrapedOpinionDocuments = opinionScraper.scrapeOpinionFiles(slipOpinions);

//		StatutesTitles[] arrayStatutesTitles = new StatutesTitles[0]; //parserInterface.getStatutesTitles();

//		StatutesTitlesArray statutesArray = blockingStatutesService.getStatutesTitles();
//		Flux<StatutesTitles> statutesArray = blockingStatutesService.getStatutesTitles();
		StatutesTitles[] arrayStatutesTitles = statutesService.getStatutesTitles().getBody();
//			codeTitles = statutesArray.getItem().toArray(codeTitles);

		OpinionDocumentParser opinionDocumentParser = new OpinionDocumentParser(arrayStatutesTitles);
		
		// this is a holds things in memory
		CitationStore citationStore = CitationStore.getInstance();
		citationStore.clearDB();

		// all memory
		for (ScrapedOpinionDocument scrapedOpinionDocument: scrapedOpinionDocuments ) {
			ParsedOpinionCitationSet parsedOpinionResults = opinionDocumentParser.parseOpinionDocument(scrapedOpinionDocument, scrapedOpinionDocument.getOpinionBase(), citationStore );
			// maybe someday deal with court issued modifications
    		opinionDocumentParser.parseSlipOpinionDetails((SlipOpinion) scrapedOpinionDocument.getOpinionBase(), scrapedOpinionDocument);
    		OpinionBase opinionBase = scrapedOpinionDocument.getOpinionBase();
    		citationStore.mergeParsedDocumentCitations(scrapedOpinionDocument.getOpinionBase(), parsedOpinionResults);
    		if ( logger.isTraceEnabled() ) {
    			logger.trace("scrapedOpinionDocument:= " 
    				+ scrapedOpinionDocument.getOpinionBase().getTitle() 
    				+ "\n	:OpinionKey= " + opinionBase.getOpinionKey()
    				+ "\n	:CountReferringOpinions= " + opinionBase.getCountReferringOpinions()
    				+ "\n	:ReferringOpinions.size()= " + (opinionBase.getReferringOpinions()== null?"xx":opinionBase.getReferringOpinions().size())
    				+ "\n	:OpinionCitations().size()= " + (opinionBase.getOpinionCitations()== null?"xx":opinionBase.getOpinionCitations().size())
    				+ "\n	:Paragraphs().size()= " + (scrapedOpinionDocument.getParagraphs()== null?"xx":scrapedOpinionDocument.getParagraphs().size())
    				+ "\n	:Footnotes().size()= " + (scrapedOpinionDocument.getFootnotes()== null?"xx":scrapedOpinionDocument.getFootnotes().size())
    				+ "\n	:OpinionTable= " + parsedOpinionResults.getOpinionTable().size()
    				+ "\n	:StatuteTable= " + parsedOpinionResults.getStatuteTable().size()
				);
    		}
		}

		List<OpinionBase> persistOpinions = new ArrayList<>();
//		List<OpinionBase> mergeOpinions = new ArrayList<>();
//		List<StatuteCitation> mergeStatutes = new ArrayList<>();	  	
		List<StatuteCitation> persistStatutes = new ArrayList<>();

		processOpinions(citationStore, persistOpinions);
	  	processStatutes(citationStore, persistStatutes);
				
    	List<OpinionStatuteCitation> persistOpinionStatuteCitations = new ArrayList<>();

		for( SlipOpinion slipOpinion: slipOpinions ) {
			if ( slipOpinion.getStatuteCitations() != null ) {
	    		for ( OpinionStatuteCitation statuteCitation: slipOpinion.getStatuteCitations() ) {
					persistOpinionStatuteCitations.add(statuteCitation);
	    		}
			}
			slipOpinionDao.save(slipOpinion);
			if ( slipOpinion.getSlipProperties() == null ) {
				System.out.println("SlipProperties == null " );
			}
			slipPropertiesDao.save(slipOpinion.getSlipProperties());
		}
		Date startTime = new Date();
    	for(OpinionBase opinion: persistOpinions ) {
			opinionBaseDao.insert(opinion);
    	}
		logger.info("Persisted "+persistOpinions.size()+" opinions in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");

		startTime = new Date();

//    	for(OpinionBase opinion: mergeOpinions ) {
//    		opinionBaseDao.update(opinion);
//    	}
//		logger.info("Merged "+mergeOpinions.size()+" opinions in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");

		startTime = new Date();
		for(OpinionStatuteCitation opinionStatuteCitation: persistOpinionStatuteCitations) {
			opinionStatuteCitationRepoistory.insert(opinionStatuteCitation);
    	}
		logger.info("Persisted "+ persistOpinionStatuteCitations.size()+" opinionStatuteCitation in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");

    	for(StatuteCitation statute: persistStatutes ) {
			if ( statute.getStatuteKey() == null ) {
			System.out.println("statute.getStatuteKey() == null");
			}
	    	for( OpinionStatuteCitation referringOpinion: statute.getReferringOpinions() ) {
	    		referringOpinion.hashCode();
	    	}
			
    		statuteCitationDao.insert(statute);
    	}
		logger.info("Persisted "+persistStatutes.size()+" statutes in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");

//		startTime = new Date();
//    	for(StatuteCitation statute: mergeStatutes ) {
//    		statuteCitationRepository.save(statute);
//    	}
//		logger.info("Merged "+mergeStatutes.size()+" statutes in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");
	}

	private void processOpinions(CitationStore citationStore,  
		List<OpinionBase> persistOpinions 
		
	) {
    	Date startTime = new Date();
		Set<OpinionBase> opinions = citationStore.getAllOpinions();
		List<OpinionKey> opinionKeys = new ArrayList<>();
		int i = 0;
		List<OpinionBase> existingOpinions = new ArrayList<>();
//		TypedQuery<OpinionBase> opinionsWithReferringOpinions = opinionBaseRepository.opinionsWithReferringOpinions(opinionKeys)pem.createNamedQuery("OpinionBase.opinionsWithReferringOpinions", OpinionBase.class);
    	for(OpinionBase opinion: opinions ) {
    		opinionKeys.add(opinion.getOpinionKey());
    		if ( ++i % 100 == 0 ) {
    			existingOpinions.addAll( opinionBaseDao.opinionsWithReferringOpinions(opinionKeys) );
    			opinionKeys.clear();
    		}
    	}
    	if ( opinionKeys.size() != 0 ) {
    		existingOpinions.addAll( opinionBaseDao.opinionsWithReferringOpinions(opinionKeys) );
    	}
    	Collections.sort(existingOpinions);
    	OpinionBase[] existingOpinionsArray = existingOpinions.toArray(new OpinionBase[existingOpinions.size()]);
    	for(OpinionBase opinion: opinions ) {
//This causes a NPE !?!?	    		
//    		opinion.checkCountReferringOpinions();
    		// checking for opinionBase for citations
    		int idx = Arrays.binarySearch(existingOpinionsArray, opinion);
//			persistOpinions.add(opinion);
    		
    		if ( idx < 0 ) {
				persistOpinions.add(opinion);
			} else {
				OpinionBase existingOpinion = existingOpinionsArray[idx]; 
				existingOpinion.mergePersistenceFromSlipLoad(opinion);
//				
//	    		logger.fine("existingOpinion:= " 
//	    				+ existingOpinion.getTitle() 
//	    				+ "\n	:OpinionKey= " + existingOpinion.getOpinionKey()
//	    				+ "\n	:CountReferringOpinions= " + existingOpinion.getCountReferringOpinions()
//	    				+ "\n	:ReferringOpinions.size()= " + (existingOpinion.getReferringOpinions()== null?"xx":existingOpinion.getReferringOpinions().size())
//	    				+ "\n	:OpinionCitations().size()= " + (existingOpinion.getOpinionCitations()== null?"xx":existingOpinion.getOpinionCitations().size())
//	    			);
//	    			
				persistOpinions.add(existingOpinion);
			}
			logger.trace("opinion "+opinion.getOpinionKey()
//				+ "\n	mergeOpinions:= " + mergeOpinions.size() 
				+ "\n	persistOpinions:= " + persistOpinions.size() 
			);

			
		}
		logger.info("Divided "+citationStore.getAllOpinions().size()+" opinions in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");
    }

    private void processStatutes( 
    		CitationStore citationStore, 
    		List<StatuteCitation> persistStatutes
	) {
    	Date startTime = new Date();
    	Set<StatuteCitation> statutes = citationStore.getAllStatutes();
		List<StatuteKey> statuteKeys = new ArrayList<>();
		int i = 0;
		List<StatuteCitation> existingStatutes = new ArrayList<>();
//		TypedQuery<StatuteCitation> statutesWithReferringOpinions 
//			= em.createNamedQuery("StatuteCitation.statutesWithReferringOpinions", StatuteCitation.class);
//		EntityGraph<?> fetchGraphForStatutesWithReferringOpinions = em.getEntityGraph("fetchGraphForStatutesWithReferringOpinions");
//		statutesWithReferringOpinions.setHint("javax.persistence.fetchgraph", fetchGraphForStatutesWithReferringOpinions);
		
		for(StatuteCitation statuteCitation: statutes ) {
    		statuteKeys.add(statuteCitation.getStatuteKey());
    		if ( ++i % 100 == 0 ) {
    			existingStatutes.addAll( statuteCitationDao.statutesWithReferringOpinions(statuteKeys));
    			statuteKeys.clear();
    		}
    	}
    	if ( statuteKeys.size() != 0 ) {
    		existingStatutes.addAll( statuteCitationDao.statutesWithReferringOpinions(statuteKeys));
    	}
    	Collections.sort(existingStatutes);
    	StatuteCitation[] existingStatutesArray = existingStatutes.toArray(new StatuteCitation[existingStatutes.size()]);
		
    	int count = statutes.size();
    	for(StatuteCitation statute: statutes ) {
    		int idx = Arrays.binarySearch(existingStatutesArray, statute);
//			persistStatutes.add(statute);
			if ( idx < 0 ) {
				persistStatutes.add(statute);
			} else {
	    		StatuteCitation existingStatute = existingStatutesArray[idx];
				existingStatute.mergeStatuteCitationFromSlipLoad(statute);
				
				persistStatutes.add(existingStatute);
			}
    	}
		logger.info("Divided "+count+" statutes in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");
    }

    public void deleteExistingOpinions(List<SlipOpinion> currentCopy) {
		logger.info("Deleting " + currentCopy.size() + " cases." );
		// need to fill out OpinionCitations and StatuteCitations for these opinions
		List<OpinionBase> citedOpinions = new ArrayList<>(); 
		List<Integer> opinionIds = new ArrayList<>();
		int i = 0;
//		TypedQuery<OpinionBase> queryCitedOpinions = em.createNamedQuery("OpinionBase.fetchCitedOpinionsWithReferringOpinions", OpinionBase.class);
		for (SlipOpinion deleteOpinion: currentCopy) {
			opinionIds.add(deleteOpinion.getId());
			if ( ++i % 100 == 0 ) {
				citedOpinions.addAll( opinionBaseDao.fetchCitedOpinionsWithReferringOpinions(opinionIds) );
				opinionIds.clear();
			}
		}
		if ( opinionIds.size() != 0 ) {
			citedOpinions.addAll( opinionBaseDao.fetchCitedOpinionsWithReferringOpinions(opinionIds) );
		}
		
		// ugly double loop ( O(n^2) )
		// but currently only getting 350 opinions max, 
		// operationally much lower if update every night
		for ( OpinionBase citedOpinion: citedOpinions ) {
			for (SlipOpinion deleteOpinion: currentCopy) {
				if ( citedOpinion != null )
					citedOpinion.removeReferringOpinion(deleteOpinion);
			}
		}
		
		opinionIds.clear();
		i = 0;
//		Query queryOpinionStatuteCitations = em.createNamedQuery("OpinionStatuteCitation.deleteOpinionStatuteCitations");
		for (SlipOpinion deleteOpinion: currentCopy) {
			opinionIds.add(deleteOpinion.getId());
			if ( ++i % 100 == 0 ) {
				opinionStatuteCitationRepoistory.deleteOpinionStatuteCitations(opinionIds);
				opinionIds.clear();
			}
		}
		if ( opinionIds.size() != 0 ) {
			opinionStatuteCitationRepoistory.deleteOpinionStatuteCitations(opinionIds);
		}
		

		for (SlipOpinion deleteOpinion: currentCopy) {
			for ( SlipProperties slipProperty: slipPropertiesDao.findAll() ) {
				if ( slipProperty.getOpinionKey().equals(deleteOpinion.getId())) {
					slipPropertiesDao.delete(slipProperty);
					break;
				}
			}
			slipOpinionDao.delete(deleteOpinion);
		}
	}
}
