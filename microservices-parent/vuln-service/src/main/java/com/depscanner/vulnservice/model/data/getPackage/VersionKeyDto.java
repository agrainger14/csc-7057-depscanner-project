package com.depscanner.vulnservice.model.data.getPackage;

import lombok.*;

/**
 * DTO representing a version (system, name and version).
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VersionKeyDto {
    private String system;
    private String name;
    private String version;
}
