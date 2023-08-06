package com.depscanner.vulnservice.model.data.Response;

import com.depscanner.vulnservice.model.data.getDependencies.EdgeDto;
import com.depscanner.vulnservice.model.data.getDependencies.NodeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatedDependencyResponse {
    private List<DependencyResponse> dependency;
    private List<EdgeDto> edges;
    private String error;
}
