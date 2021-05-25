package com.github.karlnicholas.legalservices.opinion.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

public class OpinionBase implements Comparable<OpinionBase>, Serializable {
	private static final long serialVersionUID = 1L;
	protected Integer id;
	protected OpinionKey opinionKey;
	protected String title;
    protected LocalDate opinionDate;
	protected Set<OpinionStatuteCitation> statuteCitations;
    protected Set<OpinionBase> opinionCitations;
    protected Set<OpinionBase> referringOpinions;
    // performance optimization equal to size of referringOpinions 
    protected int countReferringOpinions;
	private boolean newlyLoadedOpinion;

    public OpinionBase() {}
	public OpinionBase(OpinionBase opinionBase) {
		this.opinionKey = opinionBase.opinionKey;
    	setTitle(opinionBase.title);
    	this.opinionDate = opinionBase.opinionDate;
    	this.statuteCitations = opinionBase.statuteCitations;
    	this.opinionCitations = opinionBase.opinionCitations;
    	this.referringOpinions = opinionBase.referringOpinions;
    	this.countReferringOpinions = opinionBase.countReferringOpinions;
    }
	public OpinionBase(OpinionKey opinionKey, String title, LocalDate opinionDate, String court) {
		this.opinionKey = opinionKey;
		setTitle(title);
    	this.opinionDate = opinionDate;
    	this.newlyLoadedOpinion = true;
    }
	// making a new OpinionBase from only a citation.
	public OpinionBase(int volume, int vset, int page) {
		this(new OpinionKey(volume, vset, page), null, null, null);
	}
	// making a new OpinionBase from only a citation.
    public OpinionBase(OpinionBase opinionBase, String volume, String vset, String page) {
    	this(new OpinionKey(volume, vset, page));
    	addReferringOpinion(opinionBase);
    	this.newlyLoadedOpinion = false;
    }
	
	/**
	 * Only meant for comparison purposes.
	 * @param opinionKey for opinion
	 */
    public OpinionBase(OpinionKey opinionKey) {
        this.opinionKey = opinionKey;
    }
	/**
	 * adds a new referringOpinion.
	 * @param opinionBase OpinionBase 
	 */
    public void addReferringOpinion(OpinionBase opinionBase) {
    	if (referringOpinions == null ) {
    		setReferringOpinions(new TreeSet<OpinionBase>());
    	}
    	referringOpinions.add(opinionBase);
        // do it the paranoid way
        countReferringOpinions = referringOpinions.size();
    }
    /**
     * adds a new referringOpinions.
     * @param opinionBases to add as referring opinions
     */
    public void addAllReferringOpinions(Collection<OpinionBase> opinionBases) {
    	if (referringOpinions == null ) {
    		setReferringOpinions(new TreeSet<OpinionBase>());
    	}
    	referringOpinions.addAll(opinionBases);
        // do it the paranoid way
        countReferringOpinions = referringOpinions.size();
    }
    /**
     * Removes a referringOpinion if it exists
     * @param opinionBase OpinionBase
     */
    public void removeReferringOpinion(OpinionBase opinionBase) {
    	if (referringOpinions != null ) {
	    	if ( referringOpinions.remove(opinionBase) ) {
	    		countReferringOpinions = referringOpinions.size();
	    	}
    	}
    }
    public void removeOpinionStatuteCitation(OpinionStatuteCitation osc) {
		if ( statuteCitations == null ) {
			return;
		}
		statuteCitations.remove(osc);
		osc.setCountReferences(osc.getCountReferences()-1);
    }
	public void addStatuteCitations(Collection<StatuteCitation> goodStatutes) {
		if ( statuteCitations == null ) {
			statuteCitations = new TreeSet<>();
		}
		for( StatuteCitation statuteCitation: goodStatutes) {
			// add on both sides ...
			OpinionStatuteCitation opinionStatuteCitation = statuteCitation.getOpinionStatuteReference(this);
			if ( opinionStatuteCitation == null ) {
				throw new RuntimeException("OpinionStatuteReference not found");
			}
			// complete the other side of the reference.
			statuteCitations.add(opinionStatuteCitation);
		}
	}
	
	@JsonIgnore
	public Collection<StatuteCitation> getOnlyStatuteCitations() {
		if ( statuteCitations == null ) {
			statuteCitations = new TreeSet<>();
		}
		Set<StatuteCitation> onlyStatuteCitations = new TreeSet<>();
		for(OpinionStatuteCitation opinionStatuteCitation: statuteCitations) {
			onlyStatuteCitations.add(opinionStatuteCitation.getStatuteCitation());
		}
		return onlyStatuteCitations;
	}	
    public Integer getId() {
		return id;
	}
    public void setId(Integer id) {
		this.id = id;
	}
    public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		if ( title != null && title.length() > 127 ) title = title.substring(0, 127);
		this.title = title;
	}
	public LocalDate getOpinionDate() {
		return opinionDate;
	}
	public void setOpinionDate(LocalDate opinionDate) {
		this.opinionDate = opinionDate;
	}
	public Set<OpinionStatuteCitation> getStatuteCitations() {
		return statuteCitations;
	}
	public void setStatuteCitations(Set<OpinionStatuteCitation> statuteCitations) {
		this.statuteCitations = statuteCitations;
	}
	@JsonInclude
	public Set<OpinionBase> getOpinionCitations() {
		return opinionCitations;
	}
	public void setOpinionCitations(Set<OpinionBase> opinionCitations) {
		this.opinionCitations = opinionCitations;
	}
	@JsonIgnore
	public Set<OpinionBase> getReferringOpinions() {
        return referringOpinions;
    }
    public void setReferringOpinions(Set<OpinionBase> referringOpinions) {
        this.referringOpinions = referringOpinions;
        countReferringOpinions = referringOpinions.size();
    }
    public int getCountReferringOpinions() {
    	return countReferringOpinions;
    }
	public void updateCountReferringOpinions() {
		countReferringOpinions = referringOpinions.size();
	}
	public void setCountReferringOpinions(int countReferringOpinions) {
		this.countReferringOpinions = countReferringOpinions;
	}
	public OpinionKey getOpinionKey() {
		return opinionKey;
	}
	public void setOpinionKey(OpinionKey opinionKey) {
		this.opinionKey = opinionKey ;
	}
	@Override
	public int hashCode() {
		return opinionKey.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
//		if (getClass() != obj.getClass())
//			return false;
		if ( !(obj instanceof OpinionBase) ) 
			return false;
		OpinionBase other = (OpinionBase) obj;
		return opinionKey.equals(other.opinionKey);
		// id may not be initialized from database during initial load historical opinions.
//		return id.equals(other.id);
	}
	@Override
	public int compareTo(OpinionBase o) {
		return opinionKey.compareTo(o.opinionKey);
	}
	@JsonIgnore
	public boolean isNewlyLoadedOpinion() {
		return newlyLoadedOpinion;
	}
	public void setNewlyLoadedOpinion(boolean newlyLoadedOpinion) {
		this.newlyLoadedOpinion = newlyLoadedOpinion;
	}
	@Override
	public String toString() {
        return String.format("%1$s : %2$tm/%2$td/%2$ty : %3$s", getOpinionKey().toString(), getOpinionDate(), getTitle() );
    }
	public String fullPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%1$s : %2$tm/%2$td/%2$ty : %3$s", getOpinionKey().toString(), getOpinionDate(), getTitle() ));
		sb.append('\n');
		sb.append("statuteCitations");
		sb.append('\n');
		for ( OpinionStatuteCitation statuteCitation: statuteCitations ) {
			sb.append(statuteCitation);
			sb.append('\n');
		};
		sb.append("opinionCitations");
		sb.append('\n');
		for ( OpinionBase opinionCitation: opinionCitations ) {
			sb.append(opinionCitation);
			sb.append('\n');
		};
		sb.append("referringOpinions");
		sb.append('\n');
		if( referringOpinions != null ) {
			for ( OpinionBase referringOpinion: referringOpinions ) {
				sb.append(referringOpinion);
				sb.append('\n');
			};
		}
		sb.append('\n');
		sb.append("countReferringOpinions");
		sb.append('\n');
		sb.append(countReferringOpinions);
		sb.append('\n');
		return sb.toString();
    }
}
