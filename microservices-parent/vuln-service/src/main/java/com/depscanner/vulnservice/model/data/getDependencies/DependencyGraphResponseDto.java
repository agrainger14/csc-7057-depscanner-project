package com.depscanner.vulnservice.model.data.getDependencies;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a deps.dev API response object containing dependency graph (related dependency) information.
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DependencyGraphResponseDto {
    private List<NodeDto> nodes;
    private List<EdgeDto> edges;
    private String error;
}
