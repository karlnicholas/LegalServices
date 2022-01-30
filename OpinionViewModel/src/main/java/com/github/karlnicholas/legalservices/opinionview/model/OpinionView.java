package com.github.karlnicholas.legalservices.opinionview.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;
import com.github.karlnicholas.legalservices.slipopinion.model.SlipOpinion;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@JsonTypeName("opinionView")
public class OpinionView implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int MAX_INFO_LENGTH = 75;
	// reverse sorted by the constructor.
	private List<StatuteView> statutes;
	// reverse sorted by the constructor.
	private List<CaseView> cases;
	private String name;
	private String title;
	private LocalDate opinionDate;
	private String fileName;
	private String disposition;
	private String summary;
	private OpinionKey opinionKey;
	private String publicationStatus;
	
	public OpinionView() {
		super();
	}
	public OpinionView(
		SlipOpinion slipOpinion, 
		String name,
		List<StatuteView> statutes, 
		List<CaseView> cases
	) {
		this.name = name;
		this.title = slipOpinion.getTitle();
		this.fileName = slipOpinion.getFileName();
		this.publicationStatus = slipOpinion.getSlipProperties().getPublicationStatus();
		this.opinionDate = slipOpinion.getOpinionDate();
		this.disposition = slipOpinion.getSlipProperties().getDisposition();
		this.opinionKey = slipOpinion.getOpinionKey();		
		this.statutes = statutes;
		this.cases = cases;
		this.setSummary(slipOpinion.getSlipProperties().getSummary());
	}
	
    @JsonInclude
    public List<SectionView> getSectionViews() {
    	List<SectionView> sectionViews = new ArrayList<>();
    	for ( StatuteView statueView: statutes ) {
    		sectionViews.addAll( statueView.getSectionViews() );
    	}
    	return sectionViews;
    }
	// supporting methods for JSF pages
    @JsonInclude
	public String getCondensedStatuteInfo() {
		StringBuilder sb = new StringBuilder();
		boolean shortened = false;
		for (StatuteView statuteView: statutes) {
			sb.append(statuteView.getShortTitle());
			sb.append("  [");
			sb.append(statuteView.getRefCount());
			sb.append("], ");
			if ( sb.length() > MAX_INFO_LENGTH ) {
				sb.delete(sb.length()-2, sb.length()-1);
				sb.append("...");
				shortened = true;
				break;
			}
		}
		if( sb.length() >= 3 && !shortened ) {
			sb.delete(sb.length()-2, sb.length()-1);
		}
		return sb.toString();
	}
	
    @JsonInclude
	public String getCondensedCaseInfo() {
		StringBuilder sb = new StringBuilder();
		boolean shortened = false;
		for (CaseView caseView: cases) {
			if ( caseView.getTitle() == null ) {
				sb.append(caseView.getCitation());
			} else {
				sb.append(caseView.getTitle());
			}
			sb.append(", ");
			if ( sb.length() > MAX_INFO_LENGTH ) {
				sb.delete(sb.length()-2, sb.length()-1);
				sb.append("...");
				shortened = true;
				break;
			}
		}
		if( sb.length() >= 3 && !shortened ) {
			sb.delete(sb.length()-2, sb.length()-1);
		}
		return sb.toString();
	}
    @JsonIgnore
    public List<StatuteView> getStatutes() {
        return statutes;
    }
    public void setStatutes(List<StatuteView> statutes) {
        this.statutes = statutes;
    }
    @JsonInclude
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    @JsonInclude
    public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
    @JsonInclude
	public List<CaseView> getCases() {
		return cases;
	}
	public void setCases(List<CaseView> cases) {
		this.cases = cases;
	}
	@Override
    public String toString() {
    	return name + " " + this.getTitle();
    }
    @JsonInclude
//	@XmlElement
	@XmlJavaTypeAdapter(LocalDateAdapter.class)
	public LocalDate getOpinionDate() {
		return opinionDate;
	}
	public void setOpinionDate(LocalDate opinionDate) {
		this.opinionDate = opinionDate;
	}
    @JsonInclude
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
    @JsonInclude
	public String getDisposition() {
		return disposition;
	}
	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}
    @JsonIgnore
	public OpinionKey getOpinionKey() {
		return opinionKey;
	}
	public void setOpinionKey(OpinionKey opinionKey) {
		this.opinionKey = opinionKey;
	}
    @JsonInclude
	public String getPublicationStatus() {
		return publicationStatus;
	}
	public void setPublicationStatus(String publicationStatus) {
		this.publicationStatus = publicationStatus;
	}
    @JsonInclude
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
}

