package com.depscanner.vulnservice.model.data.getDependencies;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class EdgeDto {
    private int fromNode;
    private int toNode;
    private String requirement;
}
