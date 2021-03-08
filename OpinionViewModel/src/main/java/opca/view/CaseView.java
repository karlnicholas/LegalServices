package opca.view;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("caseView")
public class CaseView implements Comparable<CaseView>, Serializable {
	private static final long serialVersionUID = 1L;
	private String title;
	private String citation;
	private LocalDate opinionDate;
	private int countReferringOpinions;
	private int score;
	private int importance;
	
	public CaseView() {}
	public CaseView(String title, String citation, LocalDate opinionDate, int countReferringOpinions) {
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
	public LocalDate getOpinionDate() {
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
