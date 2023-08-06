package com.depscanner.vulnservice.model.data.getVersion;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DependencyDto {
    private String name;
    private String version;
    private String system;
}