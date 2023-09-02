package com.depscanner.vulnservice.event;

import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryKeyDto;
import com.depscanner.vulnservice.model.data.getVersion.DependencyDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Data class representing a vulnerable dependency along with associated advisory keys.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VulnDependency {
    private DependencyDto dependency;
    private Set<AdvisoryKeyDto> advisoryKeys;
}
