package statutes.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import statutes.StatutesRoot;
import statutes.StatutesTitles;
import statutes.service.dto.KeyHierarchyPair;
import statutes.service.dto.StatuteHierarchy;
import statutes.service.dto.StatuteKey;

public interface StatutesService {

	String STATUTES = "statutes";
	String STATUTESTITLES = "statutestitles";
	String STATUTEHIERARCHY = "statutehierarchy";
	String STATUTESANDHIERARCHIES = "statutesandhierarchies";

	Flux<StatutesRoot> getStatutesRoots();

	Flux<StatutesTitles> getStatutesTitles();

	Mono<StatuteHierarchy> getStatuteHierarchy(String fullFacet);

	Flux<KeyHierarchyPair> getStatutesAndHierarchies(Flux<StatuteKey> statuteKeys);

}
