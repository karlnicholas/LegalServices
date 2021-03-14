package com.github.karlnicholas.legalservices.statute.service.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.github.karlnicholas.legalservices.statute.service.StatuteService;

@Configuration
public class StatutesServiceRouter {
	@Bean
	  public RouterFunction<ServerResponse> route(StatutesServiceHandler statutesServiceHandler) {
	    return RouterFunctions
	      .route(RequestPredicates.GET(StatuteService.STATUTES)
	    		  .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), statutesServiceHandler::getStatutesRoots)
	      .andRoute(RequestPredicates.GET(StatuteService.STATUTESTITLES)
	    		  .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), statutesServiceHandler::getStatutesTitles)
	      .andRoute(RequestPredicates.GET(StatuteService.STATUTEHIERARCHY)
	    		  .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), statutesServiceHandler::getStatuteHierarchy)
	      .andRoute(RequestPredicates.GET(StatuteService.STATUTESANDHIERARCHIES)
	    		  .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), statutesServiceHandler::getStatutesAndHierarchies)
	      .andRoute(RequestPredicates.POST(StatuteService.STATUTESANDHIERARCHIES)
	    		  .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), statutesServiceHandler::getStatutesAndHierarchies)
	      ;
	  }

}
