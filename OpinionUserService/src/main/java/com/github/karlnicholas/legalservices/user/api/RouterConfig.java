package com.github.karlnicholas.legalservices.user.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(
            AuthHandler authHandler,
            ApplicationUserHandler userHandler
    ) {
        return RouterFunctions.nest(accept(MediaType.APPLICATION_JSON),
             RouterFunctions.route(POST("/signin"), authHandler::handleLogin)
    		.andRoute(POST("/signup"),  authHandler::handleNewUser)
			.andRoute(GET("/profile"), userHandler::handleUser)
            .andRoute(POST("/profile").and(contentType(MediaType.APPLICATION_JSON)), userHandler::updateUser))
		;

//        return RouterFunctions.route(POST("/signin").and(accept(MediaType.APPLICATION_JSON)), authHandler::handleLogin)
//    		.andRoute(POST("/signup").and(accept(MediaType.APPLICATION_JSON)),  authHandler::handleNewUser)
//			.andRoute(GET("/profile").and(accept(MediaType.APPLICATION_JSON)), userHandler::handleUser)
//            .andRoute(POST("/profile").and(accept(MediaType.APPLICATION_JSON)), userHandler::updateUser)
//		;
    }
}