package com.github.karlnicholas.legalservices.opinion.parser;

import java.util.ArrayList;
import java.util.List;

import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;

public class ScrapedOpinionDocument {
	private boolean scrapedSuccess;
	private List<String> paragraphs; 
	private List<String> footnotes;
	private final OpinionBase opinionBase;
	
	public ScrapedOpinionDocument(OpinionBase opinionBase) {
		this.opinionBase = opinionBase;
		setScrapedSuccess(false);
		paragraphs = new ArrayList<String>(); 
		footnotes = new ArrayList<String>();
	}

	public List<String> getParagraphs() {
		return paragraphs;
	}

	public void setParagraphs(List<String> paragraphs) {
		this.paragraphs = paragraphs;
	}

	public List<String> getFootnotes() {
		return footnotes;
	}

	public void setFootnotes(List<String> footnotes) {
		this.footnotes = footnotes;
	}

	public OpinionBase getOpinionBase() {
		return opinionBase;
	}

	public boolean isScrapedSuccess() {
		return scrapedSuccess;
	}

	public void setScrapedSuccess(boolean scrapedSuccess) {
		this.scrapedSuccess = scrapedSuccess;
	}
	
}
