package com.depscanner.notificationservice.event;

import lombok.*;

import java.util.List;

/**
 * Data class representing a vulnerable dependency along with associated advisory keys.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VulnDependency {
    private DependencyDto dependency;
    private List<AdvisoryKeyDto> advisoryKeys;
}