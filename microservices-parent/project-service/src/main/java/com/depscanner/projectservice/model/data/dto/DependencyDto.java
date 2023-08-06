package com.depscanner.projectservice.model.data.dto;

import com.depscanner.projectservice.model.enumeration.BuildToolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DependencyDto {
    private String id;
    private String name;
    private String version;
    private BuildToolType system;
    private Boolean isDevDependency;
    private Boolean isVulnerable;
}
