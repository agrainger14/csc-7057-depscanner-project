package com.depscanner.projectservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient Builder Config with Load Balancing to make requests to services enabled on Eureka Server
 */
@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced
    public static WebClient.Builder webClient() {
        return WebClient.builder();
    }
}

