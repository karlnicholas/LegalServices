package com.github.karlnicholas.legalservices.user.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ApplicationRouter {

    @Bean
    public RouterFunction<ServerResponse> routes(
            AuthHandler authHandler,
            ApplicationUserHandler userHandler
    ) {
        return RouterFunctions.route(POST("/api/auth/signin").and(accept(MediaType.APPLICATION_JSON)), authHandler::handleLogin)
    		.andRoute(POST("/api/auth/signup").and(accept(MediaType.APPLICATION_JSON)),  authHandler::handleNewUser)
			.andRoute(GET("/api/user").and(accept(MediaType.APPLICATION_JSON)), userHandler::handleUser)
		;
    }
}