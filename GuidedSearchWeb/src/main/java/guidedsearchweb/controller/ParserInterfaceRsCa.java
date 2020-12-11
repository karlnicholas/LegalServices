package guidedsearchweb.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;
import statutes.StatutesRoot;
import statutes.StatutesTitles;
import statutes.service.StatutesService;
import statutes.service.client.StatutesServiceClientImpl;

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

	public Mono<List<StatutesRoot>> getStatutes() {
		return statutesService.getStatutesRoots().map(ResponseEntity::getBody);
	}

	public Mono<StatutesTitles[]> getStatutesTitles() {
		return statutesService.getStatutesTitles().map(ResponseEntity::getBody);
	}

	public Mono<StatutesRoot> getStatutesHierarchy(String fullFacet) {
		return statutesService.getStatuteHierarchy(fullFacet).map(ResponseEntity::getBody);
	}

}
