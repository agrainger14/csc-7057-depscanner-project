package com.depscanner.vulnservice.event;

import com.depscanner.vulnservice.model.data.Response.ProjectResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Represents an event indicating the discovery of advisories for a specific user and their associated project.
 * This event encapsulates information about the user's email, project response, and vulnerable dependencies.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvisoryFoundEvent {
    private String userEmail;
    private ProjectResponse projectResponse;
    private Set<VulnDependency> vulnDependencies;
}
