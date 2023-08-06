package com.depscanner.vulnservice.model.data.getDependencies;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DependencyGraphResponseDto {
    private List<NodeDto> nodes;
    private List<EdgeDto> edges;
    private String error;
}
