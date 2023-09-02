package com.depscanner.vulnservice.model.data.Response;

import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryResponse;
import com.depscanner.vulnservice.model.data.getVersion.DependencyDto;
import com.depscanner.vulnservice.model.data.getVersion.LinkDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DependencyResponse {
    private DependencyDto versionKey;
    private Boolean bundled;
    private String relation;
    private List<AdvisoryResponse> advisoryDetail;
    private List<String> licenses;
    private List<LinkDto> links;
    private LocalDateTime publishedAt;
    private List<String> errors;
}
