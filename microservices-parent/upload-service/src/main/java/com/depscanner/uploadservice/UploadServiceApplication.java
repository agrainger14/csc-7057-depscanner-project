package com.depscanner.uploadservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * The main entry point for the UploadService application.
 * This class runs the Spring Boot application, enabling discovery client functionality
 * and providing OpenAPI documentation for the upload-service API.
 * @see SpringBootApplication
 * @see EnableDiscoveryClient
 * @see OpenAPIDefinition
 */
@SpringBootApplication
@EnableDiscoveryClient
@OpenAPIDefinition(info =
@Info(title = "Upload API", version = "1.0", description = "Upload Documentation API v1.0")
)
public class UploadServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UploadServiceApplication.class, args);
	}

}
