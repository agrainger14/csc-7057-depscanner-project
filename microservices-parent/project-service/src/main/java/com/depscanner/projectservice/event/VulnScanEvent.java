package com.depscanner.projectservice.event;

import com.depscanner.projectservice.model.data.dto.DependencyDto;
import com.depscanner.projectservice.model.data.response.ProjectResponse;
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
