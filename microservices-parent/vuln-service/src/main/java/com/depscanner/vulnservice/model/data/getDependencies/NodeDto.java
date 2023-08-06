package com.depscanner.vulnservice.model.data.getDependencies;

import com.depscanner.vulnservice.model.data.getVersion.DependencyDto;
import lombok.*;

import java.util.List;
import java.util.Set;

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
