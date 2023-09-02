package com.depscanner.vulnservice.model.data.getPackage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a deps.dev API response object containing package (dependency) version information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageResponseDto {
    private PackageKeyDto packageKey;
    private List<VersionDto> versions;
}
