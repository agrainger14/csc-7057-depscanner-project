package com.depscanner.uploadservice.model.response;

import com.depscanner.uploadservice.model.enumeration.BuildToolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class representing a response containing information about an open-source software dependency.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DependencyResponse {
    private String name;
    private String version;
    private BuildToolType system;
    private Boolean isDevDependency;
}
