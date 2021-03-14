package com.github.karlnicholas.legalservices.statute.lucenemodel;

import com.github.karlnicholas.legalservices.statute.SectionNumberPosition;

public class LuceneSectionModel {
	private SectionNumberPosition sectionNumberPosition;
	private String sectionParagraph;
	
	public LuceneSectionModel(SectionNumberPosition sectionNumber, String sectionParagraph ) {
		this.sectionNumberPosition = sectionNumber;
		this.sectionParagraph = sectionParagraph;
	}

	public SectionNumberPosition getSectionNumberPosition() {
		return sectionNumberPosition;
	}

	public String getSectionParagraph() {
		return sectionParagraph;
	}

}
