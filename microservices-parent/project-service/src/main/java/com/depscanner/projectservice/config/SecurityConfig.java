package com.depscanner.projectservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


/**
 * Security Configuration for OAuth2 and JWT service provided by Keycloak Auth Service
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/actuator/**",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "configuration/ui",
                        "configuration/security",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/webjars/**"
                )
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt();
        return http.build();
    }
}
