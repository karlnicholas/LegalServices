package com.github.karlnicholas.legalservices.user.security.config;

import com.github.karlnicholas.legalservices.user.security.service.ApplicationUserService;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.Optional;
import java.util.function.Function;

import javax.security.auth.login.AccountLockedException;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * WebSecurityConfig class
 *
 * @author Erik Amaru Ortiz
 * @author Karl Nicholas
 */
@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {
    private final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
    private final ApplicationUserService applicationUserService;

    private final JwtParser jwtParser;
    @Value("${app.public_routes}")
    private String[] publicRoutes;
    
    public WebSecurityConfig(Key secretKey, ApplicationUserService applicationUserService) {
    	this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
        this.applicationUserService = applicationUserService;
    }
    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager authManager) {
        http.csrf()
                .disable().authorizeExchange()
                .anyExchange().permitAll();
        return http.build();
//        return http
//                .authorizeExchange()
//                    .pathMatchers(HttpMethod.OPTIONS)
//                        .permitAll()
//                    .pathMatchers(publicRoutes)
//                        .permitAll()
//                    .pathMatchers( "/favicon.ico")
//                        .permitAll()
//                    .anyExchange()
//                        .authenticated()
//                    .and()
//                .csrf()
//                    .disable()
//                .httpBasic()
//                    .disable()
//                .formLogin()
//                    .disable()
//                .exceptionHandling()
//                    .authenticationEntryPoint((swe, e) -> {
//                        logger.info("[1] Authentication error: Unauthorized[401]: " + e.getMessage());
//
//                        return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
//                    })
//                    .accessDeniedHandler((swe, e) -> {
//                        logger.info("[2] Authentication error: Access Denied[401]: " + e.getMessage());
//
//                        return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
//                    })
//                .and()
//                .addFilterAt(createAuthenticationFilter(authManager, AppServerAuthenticationConverter::getBearerToken), SecurityWebFiltersOrder.AUTHENTICATION)
//                .addFilterAt(createAuthenticationFilter(authManager, AppServerAuthenticationConverter::getCookieToken), SecurityWebFiltersOrder.AUTHENTICATION)
//                .build();
    }

    AuthenticationWebFilter createAuthenticationFilter(ReactiveAuthenticationManager authManager, Function<ServerWebExchange, Optional<String>> extractTokenFunction) {
        AuthenticationWebFilter authenticationFilter = new AuthenticationWebFilter(authManager);
        authenticationFilter.setServerAuthenticationConverter( new AppServerAuthenticationConverter(jwtParser, extractTokenFunction));
        authenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));
        return authenticationFilter;
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
    	return new ReactiveAuthenticationManager() {
			@Override
			public Mono<Authentication> authenticate(Authentication authentication) {
				return applicationUserService.getUser((String)authentication.getPrincipal())
                    .filter(user -> user.isEnabled())
                    .switchIfEmpty(Mono.error(new AccountLockedException ("User account is disabled.")))
                    .map(user -> authentication);
			}
    	};
    }

}
