package com.depscanner.vulnservice.model.data.getAdvisory;

import lombok.*;

import java.util.Set;

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
