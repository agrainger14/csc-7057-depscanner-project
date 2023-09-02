package com.depscanner.vulnservice.model.data.getDependencies;

import com.depscanner.vulnservice.model.data.getVersion.DependencyDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing a node for plotting a node graph.
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class NodeDto {
    private DependencyDto versionKey;
    private Boolean bundled;
    private String relation;
    private List<String> errors;
}
