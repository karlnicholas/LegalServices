package statutes.api;

import java.util.List;
import java.util.Map;

import statutes.SectionNumber;
import statutes.StatutesBaseClass;
import statutes.StatutesRoot;
import statutes.StatutesTitles;

public interface IStatutesApi {
	public List<StatutesRoot> getStatutes();
	
    public StatutesBaseClass findReference(String lawCode, SectionNumber sectionNumber);
    public StatutesTitles[] getStatutesTitles();
    public String getShortTitle(String lawCode);
	public String getTitle(String lawCode);
    public Map<String, StatutesTitles> getMapStatutesToTitles();
    
    public boolean loadStatutes();	// no exceptions allowed

	public StatutesRoot findReferenceByLawCode(String lawCode);
    
	public StatutesRoot getStatutesHierarchy(String lawCode);


}