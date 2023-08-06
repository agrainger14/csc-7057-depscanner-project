package com.depscanner.notificationservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvisoryFoundEvent {
    private String userEmail;
    private ProjectResponse projectResponse;
    private Set<VulnDependency> vulnDependencies;
}
