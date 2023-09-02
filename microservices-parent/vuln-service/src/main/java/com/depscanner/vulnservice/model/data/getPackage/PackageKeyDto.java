package com.depscanner.vulnservice.model.data.getPackage;

import lombok.*;

/**
 * DTO representing a package key (dependency name and system).
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageKeyDto {
    private String system;
    private String name;
}
