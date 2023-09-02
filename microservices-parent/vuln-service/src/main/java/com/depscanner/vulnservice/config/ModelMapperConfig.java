package com.depscanner.vulnservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for creating a ModelMapper bean.
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Creates and configures a ModelMapper instance.
     *
     * @return A ModelMapper instance for object-to-object mapping.
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
