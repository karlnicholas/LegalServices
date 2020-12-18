package statutes.service.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import statutes.service.ReactiveStatutesService;

@Configuration
public class StatutesServiceRouter {

	  @Bean
	  public RouterFunction<ServerResponse> route(StatutesServiceHandler statutesServiceHandler) {
	    return RouterFunctions
	      .route(RequestPredicates.GET(ReactiveStatutesService.STATUTES)
	    		  .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), statutesServiceHandler::getStatutesRoots)
	      .andRoute(RequestPredicates.GET(ReactiveStatutesService.STATUTESTITLES)
	    		  .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), statutesServiceHandler::getStatutesTitles)
	      .andRoute(RequestPredicates.GET(ReactiveStatutesService.STATUTEHIERARCHY)
	    		  .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), statutesServiceHandler::getStatuteHierarchy)
	      .andRoute(RequestPredicates.GET(ReactiveStatutesService.STATUTESANDHIERARCHIES)
	    		  .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), statutesServiceHandler::getStatutesAndHierarchies)
	      ;
	  }

}
