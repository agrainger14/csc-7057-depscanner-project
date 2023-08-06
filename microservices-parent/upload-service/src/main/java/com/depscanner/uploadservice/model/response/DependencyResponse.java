package com.depscanner.uploadservice.model.response;

import com.depscanner.uploadservice.model.enumeration.BuildToolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
