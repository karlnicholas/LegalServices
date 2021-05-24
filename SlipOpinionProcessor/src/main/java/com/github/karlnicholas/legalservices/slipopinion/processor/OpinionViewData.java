package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;
import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;

@Component
public class OpinionViewData {
	private final Logger log = LoggerFactory.getLogger(OpinionViewData.class); 
    private final List<LocalDate[]> dateBrackets;
    private final List<OpinionView> opinionViews;

    public OpinionViewData() {
        dateBrackets = new ArrayList<>();
        opinionViews = new ArrayList<>();    
    	
    }
	public List<LocalDate[]> getDateBrackets() {
		return dateBrackets;
	}

	public synchronized void addOpinionView(OpinionView opinionView) {
		if ( opinionView.getOpinionDate() == null ) {
			log.warn("Invalid opinionView date: {}", opinionView.getOpinionDate());
			return;
		}
		opinionViews.add(opinionView);		
		resetReportDates();
	}

	private void resetReportDates() {
		dateBrackets.clear();
		// do the work.

		Collections.sort(opinionViews, (ov1, ov2)->{
			return ov2.getOpinionDate().compareTo(ov1.getOpinionDate());
		});

		for (OpinionView opinionView: opinionViews) {
			LocalDate[] week = getWeekForDate(opinionView.getOpinionDate());
			if ( !bracketContains(week)) {
				dateBrackets.add(week);
			}
		}
		Collections.sort(dateBrackets, (w1, w2)->{
			return w2[0].compareTo(w1[0]);
		});
	}
	
	private LocalDate[] getWeekForDate(LocalDate date) {
		LocalDate[] week = new LocalDate[2];
		week[0] = date.with(WeekFields.of(Locale.US).dayOfWeek(), 1);
		week[1] = week[0].plusWeeks(1);
		return week;
	}
	
	private boolean bracketContains(LocalDate[] week)  {
		for ( LocalDate[] bWeek: dateBrackets) {
			if ( bWeek[0].equals(week[0]))
				return true;
		}
		return false;
	}

	private Optional<LocalDate[]> findDateBracket(LocalDate startDate) {
		if (dateBrackets == null || dateBrackets.isEmpty() ) 
			return Optional.empty();
		else {
			return dateBrackets.stream().filter(db->db[0].compareTo(startDate) <= 0 && db[1].compareTo(startDate) >= 0).findAny();
		}
					
	}
	public List<OpinionView> getOpinionViews() {
		if ( dateBrackets.size() > 0 ) {
			return getOpinionViews(dateBrackets.get(0)[0]);
		} else {
			return Collections.emptyList();
		}
	}
	public List<OpinionView> getOpinionViews(LocalDate startDate) {
		return findDateBracket(startDate).map(dates->{
			return opinionViews.stream()
			.filter(ov->ov.getOpinionDate().compareTo(dates[0]) >= 0 && ov.getOpinionDate().compareTo(dates[1]) <= 0)
			.sorted((ov1, ov2)->ov2.getOpinionDate().compareTo(ov1.getOpinionDate()))
			.collect(Collectors.toList());			
		}).orElseGet(()->Collections.emptyList());
	}
	public void deleteOpinionView(OpinionKey opinionKey) {
		opinionViews.stream().filter(opinionView->opinionView.getOpinionKey().compareTo(opinionKey) == 0).findAny().ifPresent(opinionView->{
			opinionViews.remove(opinionView);
			resetReportDates();
		});
	}
	
}
