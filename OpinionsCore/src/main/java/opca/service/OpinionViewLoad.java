package opca.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.SlipOpinion;
import opca.model.SlipProperties;
import opca.parser.ParsedOpinionCitationSet;
import opca.repository.OpinionBaseRepository;
import opca.repository.SlipOpinionRepository;
import opca.repository.SlipPropertiesRepository;
import opca.view.OpinionView;
import opca.view.OpinionViewBuilder;
import statutes.service.StatutesService;

@Component
public class OpinionViewLoad {
	Logger logger = LoggerFactory.getLogger(OpinionViewLoad.class);
	private final OpinionBaseRepository opinionBaseRepository;
	private final SlipOpinionRepository slipOpinionRepository;
	private final SlipPropertiesRepository slipPropertiesRepository;
	
	public OpinionViewLoad(OpinionBaseRepository opinionBaseRepository, 
			SlipOpinionRepository slipOpinionRepository, 
			SlipPropertiesRepository slipPropertiesRepository) {
		this.opinionBaseRepository = opinionBaseRepository;
		this.slipOpinionRepository = slipOpinionRepository;
		this.slipPropertiesRepository = slipPropertiesRepository;
	}

	@Async
	public void load(OpinionViewData opinionViewData, StatutesService statutesService) {
		// prevent all exceptions from leaving @Asynchronous block
		try {
			logger.info("load start");
			opinionViewData.setLoaded( true );
			opinionViewData.setReady( false );
			buildOpinionViews(opinionViewData, statutesService);
			opinionViewData.setStringDateList();
			opinionViewData.setReady( true );
			opinionViewData.setLoaded( false );
			logger.info("load finish: " + opinionViewData.getOpinionViews().size());
		} catch ( Exception ex ) {
			opinionViewData.setLoaded( false );
//			logger.info("load failed: " + ex.getCause().getMessage());
			logger.info("load failed: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void loadNewOpinions(OpinionViewData opinionViewData, List<OpinionKey> opinionKeys, StatutesService statutesService) {
		try {
			logger.info("loadNewOpinions start: " + opinionKeys.size());
			opinionViewData.setLoaded( true );
			opinionViewData.setReady( false );
			buildNewOpinionViews(opinionViewData, opinionKeys, statutesService);
			opinionViewData.setStringDateList();
			opinionViewData.setReady( true );
			opinionViewData.setLoaded( true );
			logger.info("loadNewOpinions finish: " + opinionViewData.getOpinionViews().size());
		} catch ( Exception ex ) {
			opinionViewData.setLoaded( false );
			logger.info("loadNewOpinions fail: " + ex.getMessage());
			
		}
	}

	private void initReportDates(OpinionViewData opinionViewData, List<Date> dates) {
		List<Date[]> reportDates = new ArrayList<Date[]>();
		// do the work.
		Calendar firstDay = Calendar.getInstance();
		Calendar lastDay = Calendar.getInstance();
		if ( dates.size() > 0 ) {
			firstDay.setTime(dates.get(0));
			lastDay.setTime(dates.get(0));
		}
		bracketWeek( firstDay, lastDay );
		Date[] currentDates = new Date[2];
		for (Date date: dates) {
			if ( testBracket(date, firstDay, lastDay)) {
				addToCurrentDates(date, currentDates);
			} else {
				reportDates.add(currentDates);
				currentDates = new Date[2];
				firstDay.setTime(date);
				lastDay.setTime(date);
				bracketWeek(firstDay, lastDay);
				addToCurrentDates(date, currentDates);
			}
		}
		opinionViewData.setReportDates(reportDates);
	}
	
	private void addToCurrentDates(Date date, Date[] currentDates) {
		if (currentDates[0] == null ) {
			currentDates[0] = date;
			currentDates[1] = date;
			return;
		} else if ( currentDates[0].compareTo(date) > 0 ) {
			currentDates[0] = date;
			return;
		} else if ( currentDates[1].compareTo(date) < 0 ) {
			currentDates[1] = date;
			return;
		}
		return;
	}
	
	private boolean testBracket(Date date, Calendar firstDay, Calendar lastDay ) {
		boolean retVal = false;
		if ( firstDay.getTime().compareTo(date) < 0 && lastDay.getTime().compareTo(date) > 0 ) return true;
		return retVal;
	}

	private void bracketWeek(Calendar firstDay, Calendar lastDay ) {
		// get today and clear time of day
		firstDay.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		firstDay.clear(Calendar.MINUTE);
		firstDay.clear(Calendar.SECOND);
		firstDay.clear(Calendar.MILLISECOND);
		firstDay.set(Calendar.DAY_OF_WEEK, firstDay.getFirstDayOfWeek());
		firstDay.getTime();		// force recomputation. 

		// get today and clear time of day
		lastDay.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		lastDay.clear(Calendar.MINUTE);
		lastDay.clear(Calendar.SECOND);
		lastDay.clear(Calendar.MILLISECOND);
		lastDay.set(Calendar.DAY_OF_WEEK, lastDay.getFirstDayOfWeek());
		// start of the next week
		lastDay.add(Calendar.WEEK_OF_YEAR, 1);
	}
	
	private void buildOpinionViews(OpinionViewData opinionViewData, StatutesService statutesService) {
		opinionViewData.setOpinionViews(new ArrayList<>());
		List<SlipOpinion> opinions = loadAllSlipOpinions();
		buildListedOpinionViews(opinionViewData, opinions, statutesService);
	}
	private void buildNewOpinionViews(OpinionViewData opinionViewData, List<OpinionKey> opinionKeys, StatutesService statutesService) {
		// remove deleted opinions from cache
		Iterator<OpinionView> ovIt = opinionViewData.getOpinionViews().iterator();
		while ( ovIt.hasNext() ) {
			OpinionView opinionView = ovIt.next();
			boolean found = false;
			for ( OpinionKey opinionKey: opinionKeys) {
				if ( opinionView.getOpinionKey().equals(opinionKey) ) {
					found = true;
					break;
				}
			}
			if ( !found ) {
				ovIt.remove();
			}
		}
		// remove existing opinions from opinionKeys list
		Iterator<OpinionKey> okIt = opinionKeys.iterator();
		while ( okIt.hasNext() ) {
			OpinionKey opinionKey = okIt.next();
			boolean found = false;
			for ( OpinionView opinionView: opinionViewData.getOpinionViews()) {
				if ( opinionView.getOpinionKey().equals(opinionKey) ) {
					found = true;
					break;
				}
			}
			if ( found ) {
				okIt.remove();
			}
		}
		if ( opinionKeys.size() > 0 ) {
			List<SlipOpinion> opinions = loadSlipOpinionsForKeys(opinionKeys);
			logger.info("opinions size " + opinions.size());
			buildListedOpinionViews(opinionViewData, opinions, statutesService);
		} else {
			logger.info("Rebuilding entire cache");
			buildOpinionViews(opinionViewData, statutesService);
		}
	}
	private void buildListedOpinionViews(OpinionViewData opinionViewData, List<SlipOpinion> opinions, StatutesService statutesService) {
		List<OpinionBase> opinionOpinionCitations = new ArrayList<>();
		List<Integer> opinionIds = new ArrayList<>();
//		TypedQuery<OpinionBase> fetchOpinionCitationsForOpinions = em.createNamedQuery("OpinionBase.fetchOpinionCitationsForOpinions", OpinionBase.class);
//		EntityGraph<?> fetchGraphForSlipOpinions = em.getEntityGraph("fetchGraphForSlipOpinions");
//		fetchOpinionCitationsForOpinions.setHint("javax.persistence.fetchgraph", fetchGraphForSlipOpinions);
		int i = 0;
		for ( SlipOpinion slipOpinion: opinions ) {
			opinionIds.add(slipOpinion.getId());
			if ( ++i % 100 == 0 ) {
				opinionOpinionCitations.addAll( 
					opinionBaseRepository.fetchOpinionCitationsForOpinions(opinionIds)
				);
				opinionIds.clear();
			}
		}
		if ( opinionIds.size() != 0 ) {
			opinionOpinionCitations.addAll( 
				opinionBaseRepository.fetchOpinionCitationsForOpinions(opinionIds)
			);
		}
		OpinionViewBuilder opinionViewBuilder = new OpinionViewBuilder(statutesService);
		for ( SlipOpinion slipOpinion: opinions ) {
			slipOpinion.setOpinionCitations( opinionOpinionCitations.get( opinionOpinionCitations.indexOf(slipOpinion)).getOpinionCitations() );
			ParsedOpinionCitationSet parserResults = new ParsedOpinionCitationSet(slipOpinion);
			OpinionView opinionView = opinionViewBuilder.buildOpinionView(slipOpinion, parserResults);
			opinionViewData.getOpinionViews().add(opinionView);
		}
		// sort results in descending date order
		Collections.sort(
			opinionViewData.getOpinionViews(), 
			(view1, view2) -> {
				int dc = view2.getOpinionDate().compareTo(view1.getOpinionDate());
				if ( dc != 0 ) return dc;
				return view1.getName().compareTo(view2.getName());
			}
		);
		// build report dates
		List<Date> dates = new ArrayList<>();
		for ( OpinionView opinionView: opinionViewData.getOpinionViews() ) {
			Date date = opinionView.getOpinionDate();
			if ( !dates.contains(date)) {
				dates.add(date);
			}
		}
		initReportDates(opinionViewData, dates);
	}

	@Autowired
	private EntityManager entityManager;
	String nQuery="select distinct \r\n" + 
			"	slipopinio0_.id as id2_0_0_, \r\n" + 
			"	opinionbas2_.id as id2_0_1_, \r\n" + 
			"	statutecit3_.opinionbase_id as opinionb2_2_2_, \r\n" + 
			"	statutecit3_.statutecitation_id as statutec3_2_2_, \r\n" + 
			"	statutecit4_.id as id1_6_3_, \r\n" + 
			"	statutecit5_.opinionbase_id as opinionb2_2_4_, \r\n" + 
			"	statutecit5_.statutecitation_id as statutec3_2_4_, \r\n" + 
			"	statutecit6_.id as id1_6_5_, \r\n" + 
			"	slipopinio0_.countreferringopinions as countref3_0_0_, \r\n" + 
			"	slipopinio0_.opiniondate as opiniond4_0_0_, \r\n" + 
			"	slipopinio0_.page as page5_0_0_, \r\n" + 
			"	slipopinio0_.volume as volume6_0_0_, \r\n" + 
			"	slipopinio0_.vset as vset7_0_0_, \r\n" + 
			"	slipopinio0_.title as title8_0_0_, \r\n" + 
			"	opinionbas2_.countreferringopinions as countref3_0_1_, \r\n" + 
			"	opinionbas2_.opiniondate as opiniond4_0_1_, \r\n" + 
			"	opinionbas2_.page as page5_0_1_, \r\n" + 
			"	opinionbas2_.volume as volume6_0_1_, \r\n" + 
			"	opinionbas2_.vset as vset7_0_1_, \r\n" + 
			"	opinionbas2_.title as title8_0_1_, \r\n" + 
			"	opinionbas2_.dtype as dtype1_0_1_, \r\n" + 
			"	opinioncit1_.referringopinions_id as referrin1_1_0__, \r\n" + 
			"	opinioncit1_.opinioncitations_id as opinionc2_1_0__, \r\n" + 
			"	statutecit3_.countreferences as countref1_2_2_, \r\n" + 
			"	statutecit3_.opinionbase_id as opinionb2_2_1__, \r\n" + 
			"	statutecit3_.statutecitation_id as statutec3_2_1__, \r\n" + 
			"	statutecit4_.designated as designat2_6_3_, \r\n" + 
			"	statutecit4_.lawcode as lawcode3_6_3_, \r\n" + 
			"	statutecit4_.sectionnumber as sectionn4_6_3_, \r\n" + 
			"	statutecit5_.countreferences as countref1_2_4_, \r\n" + 
			"	statutecit5_.opinionbase_id as opinionb2_2_2__, \r\n" + 
			"	statutecit5_.statutecitation_id as statutec3_2_2__, \r\n" + 
			"	statutecit6_.designated as designat2_6_5_, \r\n" + 
			"	statutecit6_.lawcode as lawcode3_6_5_, \r\n" + 
			"	statutecit6_.sectionnumber as sectionn4_6_5_ \r\n" + 
			"from opinionbase slipopinio0_ \r\n" + 
			"left outer join opinionbase_opinioncitations opinioncit1_ on slipopinio0_.id=opinioncit1_.referringopinions_id \r\n" + 
			"left outer join opinionbase opinionbas2_ on opinioncit1_.opinioncitations_id=opinionbas2_.id \r\n" + 
			"left outer join opinionstatutecitation statutecit3_ on opinionbas2_.id=statutecit3_.opinionbase_id \r\n" + 
			"left outer join statutecitation statutecit4_ on statutecit3_.statutecitation_id=statutecit4_.id \r\n" + 
			"left outer join opinionstatutecitation statutecit5_ on slipopinio0_.id=statutecit5_.opinionbase_id \r\n" + 
			"left outer join statutecitation statutecit6_ on statutecit5_.statutecitation_id=statutecit6_.id \r\n" + 
			"where slipopinio0_.dtype=-1344462334\r\n" + 
			"";
	
	private List<SlipOpinion> loadAllSlipOpinions() {
		// just get all slip opinions
//		EntityGraph<?> fetchGraphForOpinionsWithJoins = entityManager.getEntityGraph("fetchGraphForOpinionsWithJoins");
		List<Tuple> l = entityManager.createNativeQuery(nQuery, Tuple.class).getResultList();
//		List<SlipOpinion> opinions = slipOpinionRepository.loadOpinionsWithJoins();
		List<SlipOpinion> opinions = slipOpinionRepository.loadOpinionsWithJoins();
		// load slipOpinion properties from the database here ... ?
// List<SlipProperties> spl = em.createNamedQuery("SlipProperties.findAll", SlipProperties.class).getResultList();
		List<SlipProperties> spl = slipPropertiesRepository.findAll();
		for ( SlipOpinion slipOpinion: opinions ) {
			slipOpinion.setSlipProperties(spl.get(spl.indexOf(new SlipProperties(slipOpinion))));
		}
		return opinions;
	}
	private List<SlipOpinion> loadSlipOpinionsForKeys(List<OpinionKey> opinionKeys) {
		// just get all slip opinions
//		EntityGraph<?> fetchGraphForOpinionsWithJoins = em.getEntityGraph("fetchGraphForOpinionsWithJoins");
//		List<SlipOpinion> opinions = em.createNamedQuery("SlipOpinion.loadOpinionsWithJoinsForKeys", SlipOpinion.class)
//				.setHint("javax.persistence.fetchgraph", fetchGraphForOpinionsWithJoins)
//				.setParameter("opinionKeys", opinionKeys)
//				.getResultList();
		List<SlipOpinion> opinions = slipOpinionRepository.loadOpinionsWithJoinsForKeys(opinionKeys);
		// load slipOpinion properties from the database here ... ?
//		List<SlipProperties> spl = em.createNamedQuery("SlipProperties.findAll", SlipProperties.class).getResultList();
		List<SlipProperties> spl = slipPropertiesRepository.findAll();
		for ( SlipOpinion slipOpinion: opinions ) {
			slipOpinion.setSlipProperties(spl.get(spl.indexOf(new SlipProperties(slipOpinion))));
		}
		return opinions;
	}

}
