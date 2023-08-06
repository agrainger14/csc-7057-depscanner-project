package com.depscanner.vulnservice.event;

import com.depscanner.vulnservice.model.data.getVersion.DependencyDto;
import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryKeyDto;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VulnDependency {
    private DependencyDto dependency;
    private Set<AdvisoryKeyDto> advisoryKeys;
}
