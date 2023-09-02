package com.depscanner.uploadservice.model.entity;

import com.depscanner.uploadservice.model.enumeration.BuildToolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an entity storing information about an open-source software dependency.
 *
 * This class encapsulates attributes related to a dependency, including its name, version,
 * associated build tool system and whether it is considered a development dependency.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DependencyEntity {
    private String name;
    private String version;
    private BuildToolType system;
    private Boolean isDevDependency;
}