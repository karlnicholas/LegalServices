package opca.service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import opca.dao.OpinionBaseDao;
import opca.dao.SlipOpinionDao;
import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.SlipOpinion;
import opca.parser.ParsedOpinionCitationSet;
import opca.view.OpinionView;
import opca.view.OpinionViewBuilder;
import statutes.service.StatutesService;

@Component
public class OpinionViewLoad {
	Logger logger = LoggerFactory.getLogger(OpinionViewLoad.class);
	private final SlipOpinionDao slipOpinionDao;
	
	public OpinionViewLoad(SlipOpinionDao slipOpinionDao) {
		this.slipOpinionDao = slipOpinionDao;
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

	private void initReportDates(OpinionViewData opinionViewData, List<LocalDate> dates) {
		List<LocalDate[]> reportDates = new ArrayList<LocalDate[]>();
		// do the work.
		LocalDate firstDay = LocalDate.now();
		LocalDate lastDay = LocalDate.now();
		if ( dates.size() > 0 ) {
			firstDay = dates.get(0);
			lastDay = dates.get(0);
		}
		bracketWeek( firstDay, lastDay );
		LocalDate[] currentDates = new LocalDate[2];
		for (LocalDate date: dates) {
			if ( testBracket(date, firstDay, lastDay)) {
				addToCurrentDates(date, currentDates);
			} else {
				reportDates.add(currentDates);
				currentDates = new LocalDate[2];
				firstDay = date;
				lastDay = date;
				bracketWeek(firstDay, lastDay);
				addToCurrentDates(date, currentDates);
			}
		}
		if ( reportDates.size() == 0 && dates.size() > 0 ) {
			reportDates.add(currentDates);
		}
		opinionViewData.setReportDates(reportDates);
	}
	
	private void addToCurrentDates(LocalDate date, LocalDate[] currentDates) {
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
	
	private boolean testBracket(LocalDate date, LocalDate firstDay, LocalDate lastDay ) {
		return (firstDay.compareTo(date) <= 0 && lastDay.compareTo(date) > 0);
	}

	private void bracketWeek(LocalDate firstDay, LocalDate lastDay ) {
		firstDay = firstDay.with(WeekFields.of(Locale.US).dayOfWeek(), 1);
		lastDay = firstDay.plusWeeks(1);
	}
	
	private void buildOpinionViews(OpinionViewData opinionViewData, StatutesService statutesService) {
		opinionViewData.setOpinionViews(new ArrayList<>());
		List<SlipOpinion> opinions = slipOpinionDao.selectAllForView();
//		buildListedOpinionViews(opinionViewData, opinions, statutesService);
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
			List<SlipOpinion> opinions = slipOpinionDao.selectFromKeysForView(opinionKeys);
			logger.info("opinions size " + opinions.size());
//			buildListedOpinionViews(opinionViewData, opinions, statutesService);
		} else {
			logger.info("Rebuilding entire cache");
			buildOpinionViews(opinionViewData, statutesService);
		}
	}
	
//	private void buildListedOpinionViews(OpinionViewData opinionViewData, List<SlipOpinion> opinions, StatutesService statutesService) {
//		List<OpinionBase> opinionOpinionCitations = new ArrayList<>();
//		List<Integer> opinionIds = new ArrayList<>();
////		TypedQuery<OpinionBase> fetchOpinionCitationsForOpinions = em.createNamedQuery("OpinionBase.fetchOpinionCitationsForOpinions", OpinionBase.class);
////		EntityGraph<?> fetchGraphForSlipOpinions = em.getEntityGraph("fetchGraphForSlipOpinions");
////		fetchOpinionCitationsForOpinions.setHint("javax.persistence.fetchgraph", fetchGraphForSlipOpinions);
//		int i = 0;
//		for ( SlipOpinion slipOpinion: opinions ) {
//			opinionIds.add(slipOpinion.getId());
//			if ( ++i % 100 == 0 ) {
//				opinionOpinionCitations.addAll( 
//					opinionBaseDao.fetchOpinionCitationsForOpinions(opinionIds)
//				);
//				opinionIds.clear();
//			}
//		}
//		if ( opinionIds.size() != 0 ) {
//			opinionOpinionCitations.addAll( 
//				opinionBaseDao.fetchOpinionCitationsForOpinions(opinionIds)
//			);
//		}
//		OpinionViewBuilder opinionViewBuilder = new OpinionViewBuilder(statutesService);
//		for ( SlipOpinion slipOpinion: opinions ) {
//			slipOpinion.setOpinionCitations( opinionOpinionCitations.get( opinionOpinionCitations.indexOf(slipOpinion)).getOpinionCitations() );
//			ParsedOpinionCitationSet parserResults = new ParsedOpinionCitationSet(slipOpinion);
//			OpinionView opinionView = opinionViewBuilder.buildOpinionView(slipOpinion, parserResults);
//			opinionViewData.getOpinionViews().add(opinionView);
//		}
//		// sort results in descending date order
//		Collections.sort(
//			opinionViewData.getOpinionViews(), 
//			(view1, view2) -> {
//				int dc = view2.getOpinionDate().compareTo(view1.getOpinionDate());
//				if ( dc != 0 ) return dc;
//				return view1.getName().compareTo(view2.getName());
//			}
//		);
//		// build report dates
//		List<LocalDate> dates = new ArrayList<>();
//		for ( OpinionView opinionView: opinionViewData.getOpinionViews() ) {
//			LocalDate date = opinionView.getOpinionDate();
////			Calendar utcDate = Calendar.getInstance();
////			utcDate.setTime(date);
////			utcDate.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
////			utcDate.clear(Calendar.MINUTE);
////			utcDate.clear(Calendar.SECOND);
////			utcDate.clear(Calendar.MILLISECOND);
//			if ( !dates.contains(date)) {
//				dates.add(date);
//			}
//		}
//		initReportDates(opinionViewData, dates);
//	}

}
