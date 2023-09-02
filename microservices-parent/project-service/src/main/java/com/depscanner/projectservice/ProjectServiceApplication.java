package com.depscanner.projectservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * The main entry point for the ProjectService application.
 * This class runs the Spring Boot application, enabling discovery client functionality
 * and providing OpenAPI documentation for the project-service API.
 * @see SpringBootApplication
 * @see EnableDiscoveryClient
 * @see OpenAPIDefinition
 */
@SpringBootApplication
@EnableDiscoveryClient
@OpenAPIDefinition(info =
@Info(title = "Project API", version = "1.0", description = "Project Documentation API v1.0")
)
@EnableScheduling
public class ProjectServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProjectServiceApplication.class, args);
	}
}
