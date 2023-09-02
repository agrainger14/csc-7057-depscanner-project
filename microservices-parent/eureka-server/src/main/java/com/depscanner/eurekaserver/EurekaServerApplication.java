package com.depscanner.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * The main class for the Eureka Server application. This class is responsible for starting the Eureka Server,
 * which acts as a service registry for registering and discovering microservices within the system.
 * @see SpringBootApplication
 * @see EnableEurekaServer
 */

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
