package com.depscanner.projectservice.model.data.response;

import com.depscanner.projectservice.model.enumeration.BuildToolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DependencyResponse {
    private String name;
    private String version;
    private BuildToolType system;
    private Boolean isDataAvailable;
}