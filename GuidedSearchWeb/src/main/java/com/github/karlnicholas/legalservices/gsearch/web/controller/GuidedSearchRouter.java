package com.github.karlnicholas.legalservices.gsearch.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
public class GuidedSearchRouter {
	@Bean
	public RouterFunction<ServerResponse> indexRouter(@Value("classpath:/static/index.html") final Resource indexHtml) {
		return RouterFunctions.route(GET("/"), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(indexHtml));
	}

	@Bean
	public RouterFunction<ServerResponse> route(GuidedSearchHandler guidedSearchHandler) {
		return RouterFunctions
			.route(RequestPredicates.GET("/api")
				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), guidedSearchHandler::getGSearch)
			;
	}

}

