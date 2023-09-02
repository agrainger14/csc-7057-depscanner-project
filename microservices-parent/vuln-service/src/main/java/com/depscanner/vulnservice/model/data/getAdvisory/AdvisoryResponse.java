package com.depscanner.vulnservice.model.data.getAdvisory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Represents a deps.dev API response object containing advisory information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvisoryResponse {
    private AdvisoryKeyDto advisoryKey;
    private String url;
    private String title;
    private Set<String> aliases;
    private double cvss3Score;
    private String cvss3Vector;
}
