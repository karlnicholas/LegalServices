package guidedsearchweb.controller;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import statutes.StatutesRoot;
import statutes.StatutesTitles;
import statutes.service.StatutesService;
import statutes.service.client.StatutesServiceClientImpl;
import statutes.service.dto.StatuteHierarchy;

public class ParserInterfaceRsCa {
	private static final String serviceURL;	
	static {
		String s = System.getenv("statutesrsservice");
		if ( s != null )
			serviceURL = s;
		else 
			serviceURL = "http://localhost:8090/";
	}

	private StatutesService statutesService;
	public ParserInterfaceRsCa() {
		statutesService = new StatutesServiceClientImpl(serviceURL);
	}

	public Flux<StatutesRoot> getStatutes() {
		Flux<StatutesRoot> statutesList = statutesService.getStatutesRoots();
		return statutesList;
	}

	public Flux<StatutesTitles> getStatutesTitles() {
		return statutesService.getStatutesTitles();
	}

	public Mono<StatuteHierarchy> getStatutesHierarchy(String fullFacet) {
		return statutesService.getStatuteHierarchy(fullFacet);
	}

}
