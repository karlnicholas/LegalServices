package com.github.karlnicholas.legalservices.gsearch.web.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GuidedSearchRouter {
	@Bean
	public RouterFunction<ServerResponse> route(GuidedSearchHandler guidedSearchHandler) {
		return RouterFunctions
			.route(RequestPredicates.GET("/api/statutes")
				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), guidedSearchHandler::getGSearch)
			;
	}

}

