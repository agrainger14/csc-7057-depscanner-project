package com.depscanner.vulnservice.model.data.getVersion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing link information for a dependency version.
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class LinkDto {
    private String label;
    private String url;
}