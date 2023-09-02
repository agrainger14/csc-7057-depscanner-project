package com.depscanner.vulnservice.event;

import com.depscanner.vulnservice.model.data.Response.ProjectResponse;
import com.depscanner.vulnservice.model.data.getVersion.DependencyDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * VulnScanEvent to send to the notification-service via Kafka Event.
 * Sends user email to notified user, along with project and dependency details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VulnScanEvent {
    private String userEmail;
    private ProjectResponse projectResponse;
    private List<DependencyDto> dependencies;
}