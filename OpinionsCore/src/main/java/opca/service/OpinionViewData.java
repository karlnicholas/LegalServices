package opca.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import opca.view.OpinionView;

@Component
public class OpinionViewData {
    private List<Date[]> reportDates;
    private List<OpinionView> opinionViews;    
	private List<String[]> stringDateList = new ArrayList<>();
	public List<String[]> getStringDateList() {
		return stringDateList;
	}
	private boolean ready = false;
	private boolean loaded = false;
	
	public synchronized List<Date[]> getReportDates() {
		return reportDates;
	}
	public synchronized void setReportDates(List<Date[]> reportDates) {
		this.reportDates = reportDates;
	}
	public synchronized List<OpinionView> getOpinionViews() {
		return opinionViews;
	}
	public synchronized void setOpinionViews(List<OpinionView> opinionViews) {
		this.opinionViews = opinionViews;
	}
	public synchronized boolean isReady() {
		return ready;
	}
	public synchronized void setReady(boolean ready) {
		this.ready = ready;
	}
	public synchronized boolean isLoaded() {
		return loaded;
	}
	public synchronized void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
	public void setStringDateList() {
		stringDateList.clear();
		SimpleDateFormat lform = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sform = new SimpleDateFormat("MMM dd");
		List<Date[]> reportDates = getReportDates();
		if ( reportDates == null )
			return;
		for ( Date[] dates: reportDates ) {
			//TODO fix this dates having null in the dates list
			if ( dates[0] == null || dates[1] == null ) continue;  
			String[] e = new String[2]; 
			e[0] = String.format("%s - %s", 
				sform.format(dates[0]),
				sform.format(dates[1]));
			e[1] = String.format("?startDate=%s", lform.format(dates[0]));
			stringDateList.add(e);	
		}
	}
}
