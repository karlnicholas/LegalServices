package com.github.karlnicholas.legalservices.user.security.config;

import com.github.karlnicholas.legalservices.user.model.ERole;
import com.github.karlnicholas.legalservices.user.security.service.ApplicationUserService;
import com.nimbusds.jose.JOSEException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

/**
 * WebSecurityConfig class
 *
 * @author Karl Nicholas
 */
@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {
    private final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
    private final ApplicationUserService applicationUserService;
    private final byte[] sharedSecret;

    public WebSecurityConfig(byte[] sharedSecret, ApplicationUserService applicationUserService) {
        this.sharedSecret = sharedSecret;
        this.applicationUserService = applicationUserService;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager authManager) throws JOSEException {
//        http.csrf()
//                .disable().authorizeExchange()
//                .anyExchange().permitAll();
//        return http.build();
        http
                // ...
                .authorizeExchange()
                // any URL that starts with /admin/ requires the role "ROLE_ADMIN"
                .pathMatchers("/api/user/admin/**").hasRole(ERole.ADMIN.name())
                // a POST to /users requires the role "USER_POST"
                .pathMatchers("/api/user").authenticated()
                // any other request requires the user to be authenticated
                .anyExchange().permitAll()
                .and()
                .csrf()
                .disable()
                .httpBasic()
                .disable()
                .formLogin()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) -> {
                    logger.info("[1] Authentication error: Unauthorized[401]: " + e.getMessage());
                    return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
                })
                .accessDeniedHandler((swe, e) -> {
                    logger.info("[2] Authentication error: Access Denied[401]: " + e.getMessage());
                    return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
                })
                .and()
                .addFilterAt(createAuthenticationFilter(authManager, AppServerAuthenticationConverter::getBearerToken), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(createAuthenticationFilter(authManager, AppServerAuthenticationConverter::getCookieToken), SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();

    }

    AuthenticationWebFilter createAuthenticationFilter(ReactiveAuthenticationManager authManager, Function<ServerWebExchange, Optional<String>> extractTokenFunction) throws JOSEException {
        AuthenticationWebFilter authenticationFilter = new AuthenticationWebFilter(authManager);
        authenticationFilter.setServerAuthenticationConverter(new AppServerAuthenticationConverter(sharedSecret, extractTokenFunction));
        authenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));
        return authenticationFilter;
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return new ReactiveAuthenticationManager() {
            @Override
            public Mono<Authentication> authenticate(Authentication authentication) {
                return Mono.justOrEmpty(applicationUserService.getUser((String) authentication.getPrincipal()).map(user->authentication));
            }
        };
    }

}
