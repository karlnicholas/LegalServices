package opca.view;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("caseView")
public class CaseView implements Comparable<CaseView> {
	private String title;
	private String citation;
	private Date opinionDate;
	private int countReferringOpinions;
	private int score;
	private int importance;
	
	public CaseView() {}
	public CaseView(String title, String citation, Date opinionDate, int countReferringOpinions) {
		this.title = title;
		this.citation = citation;
		this.opinionDate = opinionDate;
		this.countReferringOpinions = countReferringOpinions;
		this.importance = 0;
	}
	public String getTitle() {
		return title;
	}
	public String getCitation() {
		return citation;
	}
	public Date getOpinionDate() {
		return opinionDate;
	}
	@JsonIgnore
	public int getCountReferringOpinions() {
		return countReferringOpinions;
	}
	public int getImportance() {
		return importance;
	}
	public void setImportance(int importance) {
		this.importance = importance;
	}
	@JsonIgnore
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	@Override
	public int compareTo(CaseView o) {
		return citation.compareTo(o.citation);
	}
	@Override
	public String toString() {
		return importance + " : " + citation + "(" + opinionDate + ")";
	}
}
