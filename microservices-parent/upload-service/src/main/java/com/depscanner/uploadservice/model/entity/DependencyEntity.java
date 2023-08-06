package com.depscanner.uploadservice.model.entity;

import com.depscanner.uploadservice.model.enumeration.BuildToolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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