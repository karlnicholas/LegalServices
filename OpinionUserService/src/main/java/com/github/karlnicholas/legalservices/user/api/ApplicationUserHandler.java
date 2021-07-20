package com.github.karlnicholas.legalservices.user.api;

import com.github.karlnicholas.legalservices.statute.StatutesRoot;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;
import com.github.karlnicholas.legalservices.statute.service.StatuteService;
import com.github.karlnicholas.legalservices.statute.service.StatutesServiceFactory;
import com.github.karlnicholas.legalservices.statute.service.client.StatuteServiceClientImpl;
import com.github.karlnicholas.legalservices.user.dto.ApplicationUserDto;
import com.github.karlnicholas.legalservices.user.security.service.ApplicationUserService;
import com.gitub.karlnicholas.legalservices.statute.service.dto.StatutesRoots;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ApplicationUserHandler {
    private final ApplicationUserService applicationUserService;
    private final StatutesRoots statutesRoots;

    public ApplicationUserHandler(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
        this.statutesRoots = StatutesServiceFactory.getStatutesServiceClient().getStatutesRoots().getBody();
    }

    @PreAuthorize("hasRole('USER')")
    public Mono<ServerResponse> handleUser(ServerRequest serverRequest) {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("No SecurityContext")))
                .flatMap(securityContext->{
                    Authentication authentication = securityContext.getAuthentication();
                    return Mono.justOrEmpty(applicationUserService.getUser(authentication.getName()).map(applicationUser->{
                        ApplicationUserDto applicationUserDto = new ApplicationUserDto();
                        applicationUserDto.setEmail(applicationUser.getEmail());
                        applicationUserDto.setFirstName(applicationUser.getFirstName());
                        applicationUserDto.setLastName(applicationUser.getLastName());
                        applicationUserDto.setCreateDate(applicationUser.getCreateDate());
                        applicationUserDto.setAllTitles(statutesRoots.getStatuteRoots().stream().map(StatutesRoot::getShortTitle).collect(Collectors.toList()));
                        applicationUserDto.setUserTitles(Arrays.stream(applicationUser.getTitles()).collect(Collectors.toList()));
                        applicationUserDto.setOptout(applicationUser.isOptout());
                        applicationUserDto.setVerified(applicationUser.isVerified());
                        applicationUserDto.setWelcomed(applicationUser.isWelcomed());
                        applicationUserDto.setRoles(new ArrayList<>(applicationUser.getRoles()));
                        applicationUserDto.setLocale(applicationUser.getLocale());
                        return applicationUserDto;
                    }));
                })
                .flatMap(applicationUserDto -> ServerResponse.ok().bodyValue(applicationUserDto))
                .onErrorResume(throwable -> ServerResponse.badRequest().bodyValue(throwable.getMessage()));
    }

}
