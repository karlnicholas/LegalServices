package com.github.karlnicholas.legalservices.gsearch.viewmodel;

import java.util.*;

import com.github.karlnicholas.legalservices.statute.StatutesBaseClass;

public class StatuteEntry extends EntryBase {
	
	private List<EntryReference> entries;
	
//	public StatuteEntry() {super(); init(); }
	public StatuteEntry( StatutesBaseClass reference) {
		super( reference, reference.getShortTitle() );
		init();
	}
	
	private void init() { entries = new ArrayList<EntryReference>(); }
	@Override
	public List<EntryReference> getEntries() { return entries; }
	@Override
	public String getText() { return getDisplayTitle();}
	@Override
	public boolean isSectionText() { return false; }
}