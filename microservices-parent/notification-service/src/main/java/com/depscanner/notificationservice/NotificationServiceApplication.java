package com.depscanner.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * The main entry point for the VulnService application.
 * This class runs the Spring Boot application, enabling discovery client functionality
 * and providing OpenAPI documentation for the vuln-service API.
 * @see SpringBootApplication
 * @see EnableDiscoveryClient
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}
