package com.depscanner.notificationservice.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectResponse {
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private String projectType;
}
