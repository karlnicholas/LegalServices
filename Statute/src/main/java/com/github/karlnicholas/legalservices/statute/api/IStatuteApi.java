package com.github.karlnicholas.legalservices.statute.api;

import java.util.List;
import java.util.Map;

import com.github.karlnicholas.legalservices.statute.SectionNumber;
import com.github.karlnicholas.legalservices.statute.StatutesBaseClass;
import com.github.karlnicholas.legalservices.statute.StatutesRoot;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;

public interface IStatuteApi {
	public List<StatutesRoot> getStatutes();
	
    public StatutesBaseClass findReference(String lawCode, SectionNumber sectionNumber);
    public StatutesTitles[] getStatutesTitles();
    public String getShortTitle(String lawCode);
	public String getTitle(String lawCode);
    public Map<String, StatutesTitles> getMapStatutesToTitles();
    
    public boolean loadStatutes();	// no exceptions allowed

	public StatutesRoot findReferenceByLawCode(String lawCode);
    
	public StatutesRoot getStatutesHierarchy(String fullFacet);


}