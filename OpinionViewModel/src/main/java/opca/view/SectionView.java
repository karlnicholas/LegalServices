package opca.view;

import java.io.Serializable;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import statutes.StatuteRange;
import statutes.StatutesBaseClass;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 5/27/12
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeName("sectionView")
public class SectionView extends ViewReference implements Serializable {
	private static final long serialVersionUID = 1L; 
	//	private static final Logger logger = Logger.getLogger(OpinionSection.class.getName());
	// This stuff holds the reference .. 
	// Which "code" it is and which section within that code is referenced
	// Also a place for the number of reference counts
	// as well as "designated," a working variable that shows how "strong" the reference is
    private String title;
    private String fullFacet;
    private StatuteRange statuteRange;
    private int refCount;
	private int score;
	private int importance;    
    private ViewReference parent;
    
    public void initialize(StatutesBaseClass statutesLeaf, int refCount, ViewReference parent) {
        title = statutesLeaf.getTitle();
        fullFacet = statutesLeaf.getFullFacet();
        this.refCount = refCount;
        statuteRange = statutesLeaf.getStatuteRange();
        this.setParent(parent);
    }

	@JsonIgnore
    public StatuteRange getStatuteRange() {
        return statuteRange;
    }

	@JsonIgnore
	@Override
    public String getTitle() {
        return title;
    }
    public void setTitle( String code) {
    	this.title = code;
    }
	@JsonIgnore
	@Override
    public int getRefCount() {
        return refCount;
    }

    public int incRefCount(int amount) {
        refCount = refCount + amount;
        return refCount;
    }

    public void setRefCount(int count) {
        refCount = count;
    }

	public void addReference(ViewReference reference) {
		// do nothing
	}

	@JsonIgnore
	@Override
	public ArrayList<ViewReference> getChildReferences() {
		// nothing to return
		return null;
	}
	
	@Override
	public void trimToLevelOfInterest(int levelOfInterest) {
		// nothing to do 
	}
	
	@Override
	public boolean iterateSections(IterateSectionsHandler handler) {
		return handler.handleOpinionSection(this);
	}
	
	@JsonIgnore
	public SectionView getSectionView() {
		return this;
	}

    public String toString() {
        return title + ":" + statuteRange + ":" + refCount;
    }

    @Override
	@JsonIgnore
	public ViewReference getParent() {
		return parent;
	}

	public void setParent(ViewReference parent) {
		this.parent = parent;
	}
	@JsonIgnore
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
    @JsonInclude
	public int getImportance() {
		return importance;
	}
	public void setImportance(int importance) {
		this.importance = importance;
	}

    @JsonInclude
	public String getFullFacet() {
		return fullFacet;
	}

    @JsonInclude
	public String getDisplayTitlePath() {
    	List<String> shortTitles = getShortTitles();
    	return shortTitles.toString().replace("[", "").replace("]", "") + ", " + title;
	}
    @JsonInclude
	public String getDisplaySections() {
		if ( statuteRange.geteNumber().getSectionNumber() == null ) {
	    	return ("§ " + statuteRange.getsNumber().toString());
		}
    	return ("§§ " + statuteRange.toString());
	}
    
	private List<String> getShortTitles() {
    	ArrayList<ViewReference> baseViews = new ArrayList<ViewReference>();
    	ViewReference parent = this.parent;
    	while ( parent != null ) {
    		baseViews.add(parent);
    		parent = parent.getParent();
    	}
		List<String> shortTitles = new ArrayList<String>();
		Collections.reverse(baseViews);
		for ( ViewReference baseView: baseViews) {
			shortTitles.add(baseView.getShortTitle());
		}
    	return shortTitles;
	}

	@Override
	@JsonIgnore
	public String getShortTitle() {
		return title;
	}
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fullFacet.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SectionView other = (SectionView) obj;
		if (fullFacet == null) {
			if (other.fullFacet != null)
				return false;
		} else if (!fullFacet.equals(other.fullFacet))
			return false;
		return true;
	}

}

