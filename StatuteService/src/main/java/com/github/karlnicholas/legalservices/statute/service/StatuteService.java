package com.github.karlnicholas.legalservices.statute.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.github.karlnicholas.legalservices.statute.StatuteKey;
import com.github.karlnicholas.legalservices.statute.StatutesRoot;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;

public interface StatuteService {

	String STATUTES = "statutes";
	String STATUTESTITLES = "statutestitles";
	String STATUTEHIERARCHY = "statutehierarchy";
	String STATUTESANDHIERARCHIES = "statutesandhierarchies";

	ResponseEntity<List<StatutesRoot>> getStatutesRoots();

	ResponseEntity<StatutesTitles[]> getStatutesTitles();

	ResponseEntity<StatutesRoot> getStatuteHierarchy(String fullFacet);

	ResponseEntity<List<StatutesRoot>> getStatutesAndHierarchies(List<StatuteKey> statuteKeys);

}
