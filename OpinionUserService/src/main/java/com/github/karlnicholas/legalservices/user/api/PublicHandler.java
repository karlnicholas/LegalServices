package com.github.karlnicholas.legalservices.user.api;

import com.github.karlnicholas.legalservices.user.dto.ApplicationUserDto;
import com.github.karlnicholas.legalservices.user.model.ApplicationUser;
import com.github.karlnicholas.legalservices.user.security.service.ApplicationUserService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class PublicHandler {
    private final ApplicationUserService applicationUserService;

    public PublicHandler(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

	public Mono<ServerResponse> handleDemoUser(ServerRequest serverRequest) {
		return ServerResponse.ok().body(serverRequest.bodyToMono(ApplicationUserDto.class)
				.map(applicationUserDto->{
					ApplicationUser applicationUser = new ApplicationUser(applicationUserDto.getUsername(), applicationUserDto.getPassword(), Collections.emptySet());
					applicationUser.setFirstName(applicationUserDto.getFirstName());
					applicationUser.setLastName(applicationUserDto.getLastName());
					return applicationUser;
				})
				.flatMap(applicationUserService::createUser)
				.map(applicationUser -> {
					ApplicationUserDto applicationUserDto = new ApplicationUserDto();
					applicationUserDto.setUsername(applicationUser.getUsername());
					applicationUserDto.setFirstName(applicationUser.getFirstName());
					applicationUserDto.setLastName(applicationUser.getLastName());
					return applicationUserDto;
				}), ApplicationUserDto.class);
	}
	public Mono<ServerResponse> handleVersion(ServerRequest serverRequest) {
		return ServerResponse.ok().bodyValue("Verion 1.0.0");
	}

}
