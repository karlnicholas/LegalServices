package model;

import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.regex.Pattern;

public class CourtListenerOpinion implements ParsedOpinion {
	private static final Pattern pattern = Pattern.compile("/");
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	private Long id;
	private String citation;
	private LocalDate dateFiled;
	private String caseName;
	private String fullCaseName;
	private String shortCaseName;
	//
    private String html_lawbox;
    private String[] opinions_cited;
    //
    private String clusterSource;
    public CourtListenerOpinion() {}
    public CourtListenerOpinion(CourtListenerCluster courtListenerCluster) {
        // http://www.courtlistener.com/api/rest/v3/clusters/1361768/
    	id = Long.valueOf(pattern.split(courtListenerCluster.getResource_uri())[7]);
    	clusterSource = courtListenerCluster.getSource();
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(courtListenerCluster.getDate_filed());
    	try { 
    		dateFiled = LocalDate.parse(format.format(courtListenerCluster.getDate_filed()));
    	} catch ( DateTimeException ex) {
    		System.out.println(cal.get(Calendar.YEAR) + ":" + cal.get(Calendar.MONTH) + ":" + cal.get(Calendar.DAY_OF_MONTH));
    		System.out.println(id);
    		System.out.println(clusterSource);
    		System.out.println(cal);
    		dateFiled = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)-1);
//    		throw ex;
    	}
		String citeOne = courtListenerCluster.getState_cite_one().replace(". ", "."); 
		String citeTwo = courtListenerCluster.getState_cite_two().replace(". ", "."); 
		String citeThree = courtListenerCluster.getState_cite_three().replace(". ", ".");
		if ( citeOne.contains("Cal.App.") ) {
			citation = citeOne;
    	} else if ( citeTwo.contains("Cal.App.") ) {
			citation = citeTwo;
    	} else if ( citeThree.contains("Cal.App.") ) {
			citation = citeThree;
    	} else if ( citeOne.contains("Cal.") && !citeOne.contains("Rptr") ) {
			citation = citeOne;
    	} else if ( citeTwo.contains("Cal.") && !citeTwo.contains("Rptr") ) {
			citation = citeTwo;
    	} else if ( citeThree.contains("Cal.") && !citeThree.contains("Rptr") ) {
			citation = citeThree;
    	}
/*		
		if ( citation == null ) {
			if ( citeOne != null && !citeOne.trim().isEmpty() ) System.out.println(citeOne);
			if ( citeTwo != null && !citeTwo.trim().isEmpty() ) System.out.println(citeTwo);
			if ( citeThree != null && !citeThree.trim().isEmpty() ) System.out.println(citeThree);
//			System.out.println(++total);
		}
*/
		caseName = courtListenerCluster.getCase_name();
		fullCaseName = courtListenerCluster.getCase_name_full();
		shortCaseName = courtListenerCluster.getCase_name_short();
	}
	//
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCitation() {
		return citation;
	}
	public void setCitation(String citation) {
		this.citation = citation;
	}
	public LocalDate getDateFiled() {
		return dateFiled;
	}
	public void setDateFiled(LocalDate dateFiled) {
		this.dateFiled = dateFiled;
	}
	public String getFullCaseName() {
		return fullCaseName;
	}
	public void setFullCaseName(String fullCaseName) {
		this.fullCaseName = fullCaseName;
	}
	public String getShortCaseName() {
		return shortCaseName;
	}
	public void setShortCaseName(String shortCaseName) {
		this.shortCaseName = shortCaseName;
	}
	public String getHtml_lawbox() {
		return html_lawbox;
	}
	public void setHtml_lawbox(String html_lawbox) {
		this.html_lawbox = html_lawbox;
	}
	public String[] getOpinions_cited() {
		return opinions_cited;
	}
	public void setOpinions_cited(String[] opinions_cited) {
		this.opinions_cited = opinions_cited;
	}
	public String getCaseName() {
		return caseName;
	}
	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}
	public String getClusterSource() {
		return clusterSource;
	}
	@Override
	public String toString() {
		return new StringBuilder("LoadOpinion: ").append(caseName).append(" (").append(citation).append(") ").append(dateFiled.toString()).toString();
	}
}