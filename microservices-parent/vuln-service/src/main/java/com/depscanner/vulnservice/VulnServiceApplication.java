package com.depscanner.vulnservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The main entry point for the VulnService application.
 * This class runs the Spring Boot application, enabling discovery client functionality
 * and providing OpenAPI documentation for the vuln-service API.
 * @see SpringBootApplication
 * @see EnableDiscoveryClient
 * @see OpenAPIDefinition
 */
@SpringBootApplication
@EnableDiscoveryClient
@OpenAPIDefinition(info =
@Info(title = "Vuln API", version = "1.0", description = "Vuln Documentation API v1.0")
)
public class VulnServiceApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(VulnServiceApplication.class, args);
	}

}
