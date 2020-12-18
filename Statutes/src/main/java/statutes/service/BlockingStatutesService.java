package statutes.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import statutes.StatutesRoot;
import statutes.StatutesTitles;
import statutes.service.dto.StatuteKey;

public interface BlockingStatutesService {

	String STATUTES = "statutes";
	String STATUTESTITLES = "statutestitles";
	String STATUTEHIERARCHY = "statutehierarchy";
	String STATUTESANDHIERARCHIES = "statutesandhierarchies";

	ResponseEntity<List<StatutesRoot>> getStatutesRoots();

	ResponseEntity<StatutesTitles[]> getStatutesTitles();

	ResponseEntity<StatutesRoot> getStatuteHierarchy(String fullFacet);

	ResponseEntity<List<StatutesRoot>> getStatutesAndHierarchies(List<StatuteKey> statuteKeys);

}
