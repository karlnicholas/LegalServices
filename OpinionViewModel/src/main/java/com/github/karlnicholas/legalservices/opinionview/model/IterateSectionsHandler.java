package com.github.karlnicholas.legalservices.opinionview.model;

// This interface allows for the iteration
// of each Section. It implements a CallBack
public interface IterateSectionsHandler {
	public boolean handleOpinionSection(SectionView section);
}
