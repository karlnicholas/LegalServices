package statutes.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;
import statutes.StatutesRoot;
import statutes.StatutesTitles;
import statutes.service.dto.StatuteKey;

public interface StatutesService {

	String STATUTES = "statutes";
	String STATUTESTITLES = "statutestitles";
	String STATUTEHIERARCHY = "statutehierarchy";
	String STATUTESANDHIERARCHIES = "statutesandhierarchies";

	Mono<ResponseEntity<List<StatutesRoot>>> getStatutesRoots();

	Mono<ResponseEntity<StatutesTitles[]>> getStatutesTitles();

	Mono<ResponseEntity<StatutesRoot>> getStatuteHierarchy(String fullFacet);

	Mono<ResponseEntity<List<StatutesRoot>>> getStatutesAndHierarchies(List<StatuteKey> statuteKeys);

}
