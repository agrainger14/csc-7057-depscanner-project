package com.depscanner.notificationservice.event;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data class representing a response for a project.
 */
@Data
public class ProjectResponse {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private String projectType;
}
