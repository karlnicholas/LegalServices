package com.github.karlnicholas.legalservices.gsearch.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;
import com.github.karlnicholas.legalservices.statute.StatutesRoot;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;
import com.github.karlnicholas.legalservices.statute.service.reactive.ReactiveStatuteService;
import com.github.karlnicholas.legalservices.statute.service.client.reactive.ReactiveStatuteServiceClientImpl;

public class ParserInterfaceRsCa {
	private static final String serviceURL;	
	static {
		String s = System.getenv("statutesrsservice");
		if ( s != null )
			serviceURL = s;
		else 
			serviceURL = "http://localhost:8090/";
	}

	private ReactiveStatuteService reactiveStatutesService;
	public ParserInterfaceRsCa() {
		reactiveStatutesService = new ReactiveStatuteServiceClientImpl(serviceURL);
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
