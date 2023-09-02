package com.depscanner.vulnservice.model.data.getDependencies;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing an edge for plotting a node graph.
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class EdgeDto {
    private int fromNode;
    private int toNode;
    private String requirement;
}
