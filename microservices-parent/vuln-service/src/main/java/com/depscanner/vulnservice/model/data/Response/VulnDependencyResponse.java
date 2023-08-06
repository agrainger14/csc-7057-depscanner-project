package com.depscanner.vulnservice.model.data.Response;

import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryResponse;
import com.depscanner.vulnservice.model.data.getVersion.DependencyDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VulnDependencyResponse {
    private DependencyDto dependency;
    private String relation;
    private Set<AdvisoryResponse> advisoryDetails;
}