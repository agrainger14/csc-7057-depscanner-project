package com.depscanner.vulnservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for creating a WebClient bean with load balancing support.
 */
@Configuration
public class WebClientConfig {

    /**
     * Creates and configures a WebClient builder with load balancing support.
     *
     * @return A WebClient.Builder instance configured with load balancing.
     */
    @Bean
    @LoadBalanced
    public static WebClient.Builder webClient() {
        return WebClient.builder();
    }
}

