package opinionsweb.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;
import statutes.StatutesRoot;
import statutes.StatutesTitles;
import statutes.service.ReactiveStatutesService;
import statutes.service.client.ReactiveStatutesServiceClientImpl;

public class ParserInterfaceRsCa {
	private static final String serviceURL;	
	static {
		String s = System.getenv("statutesrsservice");
		if ( s != null )
			serviceURL = s;
		else 
			serviceURL = "http://localhost:8090/";
	}

	private ReactiveStatutesService reactiveStatutesService;
	public ParserInterfaceRsCa() {
		reactiveStatutesService = new ReactiveStatutesServiceClientImpl(serviceURL);
	}

	public Mono<List<StatutesRoot>> getStatutes() {
		return reactiveStatutesService.getStatutesRoots().map(ResponseEntity::getBody);
	}

	public Mono<StatutesTitles[]> getStatutesTitles() {
		return reactiveStatutesService.getStatutesTitles().map(ResponseEntity::getBody);
	}

	public Mono<StatutesRoot> getStatutesHierarchy(String fullFacet) {
		return reactiveStatutesService.getStatuteHierarchy(fullFacet).map(ResponseEntity::getBody);
	}

}
