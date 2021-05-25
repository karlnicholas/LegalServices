package com.github.karlnicholas.legalservices.opinion.model;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.karlnicholas.legalservices.statute.StatuteKey;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 5/27/12
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */

public class StatuteCitation implements Comparable<StatuteCitation>, Serializable { 
	private static final long serialVersionUID = 1L;

	private Integer id;
	
    private StatuteKey statuteKey;

	private Set<OpinionStatuteCitation> referringOpinions;
    
    private boolean designated;
    
    public StatuteCitation() {
        referringOpinions = new TreeSet<OpinionStatuteCitation>();
    }
    
    public StatuteCitation(OpinionBase opinionBase, String lawCode, String sectionNumber) {
    	// this is constructed without a parent and that's added later
    	// when we build the hierarchy
//    	logger.fine("title:" + title + ":section:" + section);
        statuteKey = new StatuteKey(lawCode, sectionNumber);
        referringOpinions = new TreeSet<OpinionStatuteCitation>();
        referringOpinions.add(new OpinionStatuteCitation(this, opinionBase, 1));
        if ( lawCode == null ) {
            designated = false;
        } else {
            designated = true;
        }
    }
    // dirty constructor for searching only
    public StatuteCitation(StatuteKey key) {
		this.statuteKey = key;
	}
	public void addOpinionCitation(OpinionStatuteCitation opinionStatuteCitation) {
		referringOpinions.add(opinionStatuteCitation);
	}
	public StatuteKey getStatuteKey() {
        return statuteKey;
    }
    public void setStatuteKey(StatuteKey statuteKey) {
        this.statuteKey = statuteKey;
    }
	@JsonIgnore
    public Set<OpinionStatuteCitation> getReferringOpinions() {
        return referringOpinions;
    }
    public void setReferringOpinions(Set<OpinionStatuteCitation> referringOpinions) {
        this.referringOpinions = referringOpinions;
    }
    public void setRefCount(OpinionBase opinionBase, int count) {    
    	OpinionStatuteCitation opinionStatuteCitation = getOpinionStatuteReference(opinionBase);
    	if ( opinionStatuteCitation != null ) {
    		opinionStatuteCitation.setCountReferences(count);
    	} else {
    		referringOpinions.add(opinionStatuteCitation);
    	}

    }
    public OpinionStatuteCitation getOpinionStatuteReference(OpinionBase opinionBase) {
    	for ( OpinionStatuteCitation opinionStatuteReference: referringOpinions) {
    		boolean c = opinionStatuteReference.getOpinionBase().equals(opinionBase);
    		if ( c ) {
				return opinionStatuteReference;
			}
    	}
    	return null;
    }
    public void incRefCount(OpinionBase opinionBase, int amount) {
    	OpinionStatuteCitation opinionStatuteCitation = getOpinionStatuteReference(opinionBase);
        if ( opinionStatuteCitation == null ) {
        	referringOpinions.add(new OpinionStatuteCitation( this, opinionBase, amount));
        } else {
        	opinionStatuteCitation.setCountReferences(opinionStatuteCitation.getCountReferences() + amount);
        }
    }
    
	@JsonIgnore
    public boolean getDesignated() {
        return designated;
    }    
    public void setDesignated( boolean designated ) {
        this.designated = designated;
    }
    @Override
	public int compareTo(StatuteCitation o) {
        return statuteKey.compareTo(o.statuteKey);
	}
	@JsonIgnore
    public Integer getId() {
    	return id;
    }
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		if ( statuteKey == null ) System.out.println("id: statuteKey: " + id +":" + statuteKey + ":" + referringOpinions.size());
	    return statuteKey.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatuteCitation other = (StatuteCitation) obj;
		return statuteKey.equals(other.statuteKey);
	}
    @Override
    public String toString() {
        return statuteKey.toString();
    }

}
