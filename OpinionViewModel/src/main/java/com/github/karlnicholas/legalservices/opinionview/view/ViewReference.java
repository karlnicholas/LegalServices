package com.github.karlnicholas.legalservices.opinionview.view;

import java.io.Serializable;
import java.util.List;

import com.github.karlnicholas.legalservices.statute.StatutesBaseClass;

public abstract class ViewReference implements Serializable {
	private static final long serialVersionUID = 1L;

	public abstract void trimToLevelOfInterest( int levelOfInterest );

	public abstract int incRefCount(int amount);
	
	public abstract List<ViewReference> getChildReferences();
	public abstract int getRefCount();
	public abstract ViewReference getParent();

    // return true to keep iterating, false to stop iteration
	public abstract boolean iterateSections( IterateSectionsHandler handler);

	public abstract String getTitle();
	public abstract String getShortTitle();
    public abstract void initialize(StatutesBaseClass statutesLeaf, int refCount, ViewReference parent);
	
}
