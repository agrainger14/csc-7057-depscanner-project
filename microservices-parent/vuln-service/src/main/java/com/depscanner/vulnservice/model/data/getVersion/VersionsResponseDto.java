package com.depscanner.vulnservice.model.data.getVersion;

import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryKeyDto;
import jakarta.persistence.ElementCollection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class VersionsResponseDto {
    private DependencyDto versionKey;
    private Boolean isDefault;

    @ElementCollection
    private List<String> licenses;

    private List<AdvisoryKeyDto> advisoryKeys;
    private List<LinkDto> links;
    private LocalDateTime publishedAt;
}
