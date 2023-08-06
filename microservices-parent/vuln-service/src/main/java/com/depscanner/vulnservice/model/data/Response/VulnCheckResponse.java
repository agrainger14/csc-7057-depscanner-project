package com.depscanner.vulnservice.model.data.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VulnCheckResponse {
    private String name;
    private String version;
    private String system;
    private Boolean isDataAvailable;
}
