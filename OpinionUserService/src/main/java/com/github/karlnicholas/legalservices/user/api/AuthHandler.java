package com.github.karlnicholas.legalservices.user.api;

import com.github.karlnicholas.legalservices.user.dto.ApplicationUserDto;
import com.github.karlnicholas.legalservices.user.dto.UserLoginDto;
import com.github.karlnicholas.legalservices.user.model.ApplicationUser;
import com.github.karlnicholas.legalservices.user.model.ERole;
import com.github.karlnicholas.legalservices.user.model.Role;
import com.github.karlnicholas.legalservices.user.security.service.ApplicationUserService;
import com.github.karlnicholas.legalservices.user.security.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class AuthHandler {
    private final AuthService authService;
    private final ApplicationUserService applicationUserService;
    private final Validator validator;

    public AuthHandler(
            @Autowired AuthService authService,
            @Autowired ApplicationUserService applicationUserService,
            @Autowired Validator validator
    ) {
        this.authService = authService;
        this.applicationUserService = applicationUserService;
        this.validator = validator;
    }

    public Mono<ServerResponse> handleLogin(ServerRequest serverRequest) {
        return authService.authenticate(serverRequest.bodyToMono(UserLoginDto.class))
                .flatMap(authResultDto->ServerResponse.ok().bodyValue(authResultDto))
                .onErrorResume(throwable -> ServerResponse.badRequest().bodyValue(throwable.getMessage()));
    }

    public Mono<ServerResponse> handleNewUser(ServerRequest serverRequest) {
        return applicationUserService.createUser(
                serverRequest.bodyToMono(ApplicationUserDto.class)
                        .map(applicationUserDto -> {
                            Errors errors = new BeanPropertyBindingResult(applicationUserDto, applicationUserDto.getClass().getName());
                            ValidationUtils.invokeValidator(validator, applicationUserDto, errors);
                            if (errors.hasErrors() ) {
                                throw new IllegalArgumentException(errors.getAllErrors().toString());
                            }
                            ApplicationUser applicationUser = new ApplicationUser(
                                applicationUserDto.getEmail(),
                                applicationUserDto.getPassword(),
                                applicationUserDto.getLocale(),
                                Collections.singleton(new Role(ERole.USER))
                            );
                            return applicationUser;
                        }))
                .map(applicationUser -> {
                    ApplicationUserDto applicationUserDto = new ApplicationUserDto();
                    applicationUserDto.setEmail(applicationUser.getEmail());
                    applicationUserDto.setLocale(applicationUser.getLocale());
                    return applicationUserDto;
                }).flatMap(applicationUserDto -> ServerResponse.ok().bodyValue(applicationUserDto))
                .onErrorResume(throwable -> ServerResponse.badRequest().bodyValue(throwable.getMessage()));
    }
}
