package com.depscanner.vulnservice.model.data.getVersion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a dependency (system, name and version).
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DependencyDto {
    private String name;
    private String version;
    private String system;
}