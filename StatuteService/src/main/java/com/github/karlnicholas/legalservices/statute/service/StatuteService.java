package com.github.karlnicholas.legalservices.statute.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.github.karlnicholas.legalservices.statute.StatuteKey;
import com.github.karlnicholas.legalservices.statute.StatutesRoot;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;
import com.gitub.karlnicholas.legalservices.statute.service.dto.StatutesRoots;

public interface StatuteService {

	String STATUTES = "statutes";
	String STATUTESTITLES = "statutestitles";
	String STATUTEHIERARCHY = "statutehierarchy";
	String STATUTESANDHIERARCHIES = "statutesandhierarchies";

	ResponseEntity<StatutesRoots> getStatutesRoots();

	ResponseEntity<StatutesTitles[]> getStatutesTitles();

	ResponseEntity<StatutesRoot> getStatuteHierarchy(String fullFacet);

	ResponseEntity<StatutesRoots> getStatutesAndHierarchies(List<StatuteKey> statuteKeys);

}
