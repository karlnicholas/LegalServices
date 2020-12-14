package opca.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.SlipOpinion;
import opca.model.SlipProperties;
import opca.parser.ParsedOpinionCitationSet;
import opca.view.OpinionView;
import opca.view.OpinionViewBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import statutes.service.StatutesService;

@Component
public class OpinionViewLoad {
	Logger logger = LoggerFactory.getLogger(OpinionViewLoad.class);
/*
//	@Asynchronous
	public void load(OpinionViewData opinionViewData, StatutesService statutesService) {
		// prevent all exceptions from leaving @Asynchronous block
		try {
			logger.info("load start");
			opinionViewData.setLoaded( true );
			opinionViewData.setReady( false );
			buildOpinionViews(opinionViewData, statutesService).publishOn(Schedulers.boundedElastic()).subscribe((opinionView)->{
				opinionViewData.setStringDateList();
				opinionViewData.setReady( true );
				opinionViewData.setLoaded( false );
				logger.info("load finish: " + opinionViewData.getOpinionViews().size());
			});
		} catch ( Exception ex ) {
			opinionViewData.setLoaded( false );
			logger.info("load failed: " + ex.getCause().getMessage());
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
	
	private Mono<OpinionView> buildOpinionViews(OpinionViewData opinionViewData, StatutesService statutesService) {
		opinionViewData.setOpinionViews(new ArrayList<>());
		List<SlipOpinion> opinions = loadAllSlipOpinions();
		return buildListedOpinionViews(opinionViewData, opinions, statutesService);
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
	private Mono<OpinionView> buildListedOpinionViews(OpinionViewData opinionViewData, List<SlipOpinion> opinions, StatutesService statutesService) {
		List<OpinionBase> opinionOpinionCitations = new ArrayList<>();
		List<Integer> opinionIds = new ArrayList<>();
		TypedQuery<OpinionBase> fetchOpinionCitationsForOpinions = em.createNamedQuery("OpinionBase.fetchOpinionCitationsForOpinions", OpinionBase.class);
		EntityGraph<?> fetchGraphForSlipOpinions = em.getEntityGraph("fetchGraphForSlipOpinions");
		fetchOpinionCitationsForOpinions.setHint("javax.persistence.fetchgraph", fetchGraphForSlipOpinions);
		int i = 0;
		for ( SlipOpinion slipOpinion: opinions ) {
			opinionIds.add(slipOpinion.getId());
			if ( ++i % 100 == 0 ) {
				opinionOpinionCitations.addAll( 
					fetchOpinionCitationsForOpinions.setParameter("opinionIds", opinionIds).getResultList()
				);
				opinionIds.clear();
			}
		}
		if ( opinionIds.size() != 0 ) {
			opinionOpinionCitations.addAll( 
				fetchOpinionCitationsForOpinions.setParameter("opinionIds", opinionIds).getResultList()
			);
		}
		List<Mono<OpinionView>> opinionViewMonos = new ArrayList<>(); 
		OpinionViewBuilder opinionViewBuilder = new OpinionViewBuilder(statutesService);
		for ( SlipOpinion slipOpinion: opinions ) {
			slipOpinion.setOpinionCitations( opinionOpinionCitations.get( opinionOpinionCitations.indexOf(slipOpinion)).getOpinionCitations() );
			ParsedOpinionCitationSet parserResults = new ParsedOpinionCitationSet(slipOpinion);
			opinionViewMonos.add(opinionViewBuilder.buildOpinionView(slipOpinion, parserResults)
					.map(opinionView->{
						opinionViewData.getOpinionViews().add(opinionView);
						return opinionView;
					}));
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
		return Mono.zip(opinionViewMonos, null);
	}

	private List<SlipOpinion> loadAllSlipOpinions() {
		// just get all slip opinions
		EntityGraph<?> fetchGraphForOpinionsWithJoins = em.getEntityGraph("fetchGraphForOpinionsWithJoins");
		List<SlipOpinion> opinions = em.createNamedQuery("SlipOpinion.loadOpinionsWithJoins", SlipOpinion.class)
				.setHint("javax.persistence.fetchgraph", fetchGraphForOpinionsWithJoins)
				.getResultList();
		// load slipOpinion properties from the database here ... ?
		List<SlipProperties> spl = em.createNamedQuery("SlipProperties.findAll", SlipProperties.class).getResultList();
		for ( SlipOpinion slipOpinion: opinions ) {
			slipOpinion.setSlipProperties(spl.get(spl.indexOf(new SlipProperties(slipOpinion))));
		}
		return opinions;
	}
	private List<SlipOpinion> loadSlipOpinionsForKeys(List<OpinionKey> opinionKeys) {
		// just get all slip opinions
		EntityGraph<?> fetchGraphForOpinionsWithJoins = em.getEntityGraph("fetchGraphForOpinionsWithJoins");
		List<SlipOpinion> opinions = em.createNamedQuery("SlipOpinion.loadOpinionsWithJoinsForKeys", SlipOpinion.class)
				.setHint("javax.persistence.fetchgraph", fetchGraphForOpinionsWithJoins)
				.setParameter("opinionKeys", opinionKeys)
				.getResultList();
		// load slipOpinion properties from the database here ... ?
		List<SlipProperties> spl = em.createNamedQuery("SlipProperties.findAll", SlipProperties.class).getResultList();
		for ( SlipOpinion slipOpinion: opinions ) {
			slipOpinion.setSlipProperties(spl.get(spl.indexOf(new SlipProperties(slipOpinion))));
		}
		return opinions;
	}
*/
}
