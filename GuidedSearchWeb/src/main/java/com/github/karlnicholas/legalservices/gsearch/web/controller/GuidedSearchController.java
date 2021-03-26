package com.github.karlnicholas.legalservices.gsearch.web.controller;

import java.io.IOException;
//import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.github.karlnicholas.legalservices.statute.service.reactive.ReactiveStatuteService;
import com.github.karlnicholas.legalservices.statute.service.client.reactive.ReactiveStatuteServiceClientImpl;
import com.github.karlnicholas.legalservices.gsearch.GSearch;
import com.github.karlnicholas.legalservices.gsearch.viewmodel.ViewModel;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@RestController
@RequestMapping("api")
@SpringBootApplication
public class GuidedSearchController {
	public static void main(String[] args) {
		SpringApplication.run(GuidedSearchController.class, args);
	}
	
	@Bean
	public RouterFunction<ServerResponse> indexRouter(@Value("classpath:/static/index.html") final Resource indexHtml) {
		return route(GET("/"), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(indexHtml));
	}
	
	@GetMapping
	protected Mono<ViewModel> doGet(@RequestParam(required = false) String path, @RequestParam(required = false) String term, @RequestParam(required = false) boolean frag) throws IOException {

		String serviceURL;	
		String s = System.getenv("statutesrsservice");
		if ( s != null )
			serviceURL = s;
		else 
			serviceURL = "http://localhost:8090/";

		ReactiveStatuteService reactiveStatutesService = new ReactiveStatuteServiceClientImpl(serviceURL);
		GSearch gsearch = new GSearch(reactiveStatutesService);
		
		Mono<ViewModel> viewModel = gsearch.handleRequest(path, term, frag);
		return viewModel;
	}
}

