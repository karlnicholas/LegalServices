package opca.service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.OpinionStatuteCitation;
import opca.model.SlipOpinion;
import opca.model.SlipProperties;
import opca.model.StatuteCitation;
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
	String nQuery="select \r\n" + 
			"o.id as o_id,\r\n" + 
			"o.countreferringopinions as o_countreferringopinions,\r\n" + 
			"o.opiniondate as o_opiniondate,\r\n" + 
			"o.page as o_page,\r\n" + 
			"o.volume as o_volume,\r\n" + 
			"o.vset as o_vset,\r\n" + 
			"o.title as o_title,\r\n" + 
			"osc.countreferences as osc_countreferences,\r\n" + 
			"sc.designated as sc_designated,\r\n" + 
			"sc.lawcode as sc_lawcode,\r\n" + 
			"sc.sectionnumber as sc_sectionnumber,\r\n" + 
			"oc.countreferringopinions as oc_countreferringopinions,\r\n" + 
			"oc.opiniondate as oc_opiniondate,\r\n" + 
			"oc.page as oc_page,\r\n" + 
			"oc.volume as oc_volume,\r\n" + 
			"oc.vset as oc_vset,\r\n" + 
			"oc.title as oc_title,\r\n" + 
			"ocosc.countreferences as ocosc_countreferences,\r\n" + 
			"ocsc.id as ocsc_id,\r\n" + 
			"ocsc.designated as ocsc_designated,\r\n" + 
			"ocsc.lawcode as ocsc_lawcode,\r\n" + 
			"ocsc.sectionnumber as ocsc_sectionnumber,\r\n" + 
			"sp.filename,\r\n" + 
			"sp.fileextension\r\n" + 
			"from opinionbase o \r\n" + 
			"left outer join opinionstatutecitation osc on osc.opinionbase_id = o.id \r\n" + 
			"left outer join statutecitation sc on sc.id = osc.statutecitation_id\r\n" + 
			"left outer join opinionbase_opinioncitations oboc on oboc.referringopinions_id = o.id\r\n" + 
			"left outer join opinionbase oc on oc.id = oboc.opinioncitations_id\r\n" + 
			"left outer join opinionstatutecitation ocosc on  ocosc.opinionbase_id = oc.id \r\n" + 
			"left outer join statutecitation ocsc on ocsc.id = ocosc.statutecitation_id\r\n" + 
			"join slipproperties sp on o.id = sp.slipopinion_id\r\n" + 
			"where o.dtype=-1344462334;\r\n" + 
			"";

	private SlipOpinion decodeTuple(Tuple tuple) {
		SlipOpinion slipOpinion = new SlipOpinion(
				tuple.get("filename").toString(), 
				tuple.get("fileextension").toString(), 
				String.valueOf(tuple.get("o_title")), 
				Date.from(LocalDate.parse(tuple.get("o_opiniondate").toString()).atStartOfDay().toInstant(ZoneOffset.UTC)), 
				"court", 
				"searchUrl"
			);
		slipOpinion.setId(Integer.valueOf(tuple.get("o_id").toString()));
		slipOpinion.setStatuteCitations(new HashSet<>());
		slipOpinion.setOpinionCitations(new HashSet<>());
		// do StatuteCitation
		if ( tuple.get("sc_lawcode") != null && tuple.get("sc_sectionnumber") != null ) {
			StatuteCitation statuteCitation = new StatuteCitation(slipOpinion, 
					tuple.get("sc_lawcode").toString(), 
					tuple.get("sc_sectionnumber").toString());
			statuteCitation.setDesignated(Boolean.parseBoolean(tuple.get("sc_designated").toString()));
			OpinionStatuteCitation opinionStatuteCitation = new OpinionStatuteCitation(statuteCitation, slipOpinion, 
					Integer.parseInt(tuple.get("osc_countreferences").toString()));
			if ( !slipOpinion.getStatuteCitations().contains(opinionStatuteCitation)) {
				slipOpinion.getStatuteCitations().add(opinionStatuteCitation); 
			}
		}
		if ( tuple.get("oc_page") != null && tuple.get("oc_volume") != null && tuple.get("oc_vset") != null ) {
			// do OpinionCitation
			OpinionKey opinionKey = new OpinionKey(
					Integer.parseInt(tuple.get("oc_page").toString()), 
					Integer.parseInt(tuple.get("oc_volume").toString()), 
					Integer.parseInt(tuple.get("oc_vset").toString())
				);
			OpinionBase opinionCitation = new OpinionBase(opinionKey);
			OpinionBase finalOpinionCitation = slipOpinion.getOpinionCitations().stream().filter(oc->oc.equals(opinionCitation)).findAny().orElseGet(
				()->{
					opinionCitation.setCountReferringOpinions(Integer.parseInt(tuple.get("oc_countreferringopinions").toString()));
					if ( tuple.get("oc_opiniondate") != null ) {
						opinionCitation.setOpinionDate(Date.from(LocalDate.parse(tuple.get("oc_opiniondate").toString()).atStartOfDay().toInstant(ZoneOffset.UTC)));
					}
					opinionCitation.setStatuteCitations(new HashSet<>());
					slipOpinion.getOpinionCitations().add(opinionCitation); 
					return opinionCitation;}
			);
			// do OpinionCitation->StatuteCitation
			if ( tuple.get("ocsc_lawcode") != null && tuple.get("ocsc_sectionnumber") != null ) {
				StatuteCitation statuteCitation = new StatuteCitation(slipOpinion, 
						tuple.get("ocsc_lawcode").toString(), 
						tuple.get("ocsc_sectionnumber").toString());
				statuteCitation.setDesignated(Boolean.parseBoolean(tuple.get("ocsc_designated").toString()));
				OpinionStatuteCitation opinionStatuteCitation = new OpinionStatuteCitation(statuteCitation, slipOpinion, 
						Integer.parseInt(tuple.get("ocosc_countreferences").toString()));		

				if ( !finalOpinionCitation.getStatuteCitations().contains(opinionStatuteCitation)) {
					finalOpinionCitation.getStatuteCitations().add(opinionStatuteCitation); 
				}
			}
		}
		
		return slipOpinion;
	}
	
	private List<SlipOpinion> loadAllSlipOpinions() {
		// just get all slip opinions
//		EntityGraph<?> fetchGraphForOpinionsWithJoins = entityManager.getEntityGraph("fetchGraphForOpinionsWithJoins");
//		List<Tuple> l = entityManager.createNativeQuery(nQuery, Tuple.class).getResultList();
//		List<SlipOpinion> opinions = slipOpinionRepository.loadOpinionsWithJoins();
//		List<SlipOpinion> opinions = slipOpinionRepository.loadOpinionsWithJoins();
		// load slipOpinion properties from the database here ... ?
//			StatutesService statutesService = new StatutesServiceClientImpl("http://localhost:8090/");
		@SuppressWarnings("unchecked")
		List<Tuple> l = entityManager.createNativeQuery(nQuery, Tuple.class).getResultList();
		List<SlipOpinion> opinions = l.stream().parallel().collect(Collectors.groupingBy(tuple->tuple.get("o_id").toString(), 
				Collectors.mapping(this::decodeTuple, Collectors.reducing((sp1, sp2)->{
					sp1.mergePublishedOpinion(sp2);
					return sp1;
				}))))
				.values().stream()
				.map(Optional::get)
				.collect(Collectors.toList());
		
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
