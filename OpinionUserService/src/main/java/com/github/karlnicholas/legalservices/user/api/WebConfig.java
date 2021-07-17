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
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    @Bean
    public RouterFunction<ServerResponse> routes(
            AuthHandler authHandler,
            ApplicationUserHandler userHandler,
            TestHandler testHandler
    ) {
        return RouterFunctions.route(POST("/api/auth/signin").and(accept(MediaType.APPLICATION_JSON)), authHandler::handleLogin)
    		.andRoute(POST("/api/auth/signup").and(accept(MediaType.APPLICATION_JSON)),  authHandler::handleNewUser)
			.andRoute(GET("/api/user").and(accept(MediaType.APPLICATION_JSON)), userHandler::handleUser)
            .andRoute(GET("/api/test/all").and(accept(MediaType.APPLICATION_JSON)), testHandler::handleAll)
		;
    }
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins("http://localhost:8081")
                .allowedMethods("GET", "POST")
                .maxAge(3600);
    }
}