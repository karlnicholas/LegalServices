package com.github.karlnicholas.legalservices.slipopinion.processor;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OpinionViewData {
    private List<LocalDate[]> dateBrackets;
    private final List<LocalDate> opinionViewDates;
    private final List<OpinionView> opinionViews;

    public OpinionViewData() {
        dateBrackets = new ArrayList<>();
        opinionViewDates = new ArrayList<>();
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
		opinionViewDates.add(opinionView.getOpinionDate());
		resetReportDates();
	}

	private void resetReportDates() {
		dateBrackets.clear();
		// do the work.
		LocalDate firstDay = LocalDate.now();
		LocalDate lastDay = LocalDate.now();
		Collections.sort(opinionViewDates, (d1, d2)->{
			return d2.compareTo(d1);
		});
		if ( opinionViewDates.size() > 0 ) {
			firstDay = opinionViewDates.get(0);
			lastDay = opinionViewDates.get(0);
		}
		firstDay = firstDay.with(WeekFields.of(Locale.US).dayOfWeek(), 1);
		lastDay = firstDay.plusWeeks(1);
		LocalDate[] currentDates = new LocalDate[2];
		for (LocalDate date: opinionViewDates) {
			if ( testBracket(date, firstDay, lastDay)) {
				addToCurrentDates(date, currentDates);
			} else {
				dateBrackets.add(currentDates);
				currentDates = new LocalDate[2];
				firstDay = date;
				lastDay = date;
				firstDay = firstDay.with(WeekFields.of(Locale.US).dayOfWeek(), 1);
				lastDay = firstDay.plusWeeks(1);
				addToCurrentDates(date, currentDates);
			}
		}
		if ( dateBrackets.size() == 0 && opinionViewDates.size() > 0 ) {
			dateBrackets.add(currentDates);
		}
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

	private Optional<LocalDate[]> findDateBracket(LocalDate startDate) {
		if (dateBrackets == null || dateBrackets.isEmpty() ) 
			return Optional.empty();
		else {
			return dateBrackets.stream().filter(db->db[0].compareTo(startDate) <= 0 && db[1].compareTo(startDate) >= 0).findAny();
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
	
//	public void setStringDateList() {
//		stringDateList.clear();
//		SimpleDateFormat lform = new SimpleDateFormat("yyyy-MM-dd");
//		SimpleDateFormat sform = new SimpleDateFormat("MMM dd");
//		List<LocalDate[]> reportDates = getReportDates();
//		if ( reportDates == null )
//			return;
//		for ( LocalDate[] dates: reportDates ) {
//			if ( dates[0] == null || dates[1] == null ) continue;  
//			String[] e = new String[2]; 
//			e[0] = String.format("%s - %s", 
//				sform.format(dates[0]),
//				sform.format(dates[1]));
////			e[1] = String.format("?startDate=%s", lform.format(dates[0]));
//			e[1] = lform.format(dates[0]);
//			stringDateList.add(e);	
//		}
//	}
}
