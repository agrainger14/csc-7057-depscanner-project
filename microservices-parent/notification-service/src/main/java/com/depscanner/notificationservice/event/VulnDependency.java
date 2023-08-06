package com.depscanner.notificationservice.event;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VulnDependency {
    private DependencyDto dependency;
    private List<AdvisoryKeyDto> advisoryKeys;
}