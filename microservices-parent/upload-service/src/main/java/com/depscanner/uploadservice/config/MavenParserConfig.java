package com.depscanner.uploadservice.config;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for the MavenParser
 */
@Configuration
public class MavenParserConfig {
    /**
     * MavenXpp3Reader used to parse dependencies from maven pom.xml files
     */
    @Bean
    public MavenXpp3Reader mavenXpp3Reader() {
        return new MavenXpp3Reader();
    }

    /**
     * HashMap used to store models to prevent repeat API calls to the Maven Repo
     */
    @Bean
    public Map<String, Model> modelMap() {
        return new HashMap<>();
    }
}
