package loadmodelnew;

import java.time.LocalDate;

import org.jsoup.nodes.Element;

public class LoadOpinionNew {
	private Long id;
	private String citation;
	private LocalDate dateFiled;
	private String caseName;
	private Element opinionElement;
	//
    private String[] opinions_cited;
    public LoadOpinionNew(Long id, String caseName, String citation, Element opinionElement) {
    	this.id = id;
    	this.caseName = caseName;
    	this.citation = citation;
    	this.setOpinionElement(opinionElement);
	}
	//
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
	@Override
	public String toString() {
		return new StringBuilder("LoadOpinion: ").append(caseName).append(" (").append(citation).append(") ").append(dateFiled.toString()).toString();
	}
	public Element getOpinionElement() {
		return opinionElement;
	}
	public void setOpinionElement(Element opinionElement) {
		this.opinionElement = opinionElement;
	}
}