package com.github.karlnicholas.legalservices.user.api;

import com.github.karlnicholas.legalservices.user.dto.ApplicationUserDto;
import com.github.karlnicholas.legalservices.user.security.service.ApplicationUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class ApplicationUserHandler {
//    private final ApplicationUserService applicationUserService;
//
//    public ApplicationUserHandler(ApplicationUserService applicationUserService) {
//        this.applicationUserService = applicationUserService;
//    }

	@PreAuthorize("hasRole('USER')")
	public Mono<ServerResponse> handleUser(ServerRequest serverRequest) {
		return ServerResponse.ok().bodyValue("test");
//		return ServerResponse.ok().body(ReactiveSecurityContextHolder.getContext()
//                .switchIfEmpty(Mono.error(new IllegalStateException("No SecurityContext")))
//                .map(SecurityContext::getAuthentication)
//                .map(Authentication::getName)
//                .flatMap(applicationUserService::getUser)
//                .map(applicationUser -> {
//                    ApplicationUserDto applicationUserDto = new ApplicationUserDto();
//                    applicationUserDto.setUsername(applicationUser.getUsername());
//                    applicationUserDto.setFirstName(applicationUser.getFirstName());
//                    applicationUserDto.setLastName(applicationUser.getLastName());
//                    return applicationUserDto;
//                }), ApplicationUserDto.class);
	}

}
