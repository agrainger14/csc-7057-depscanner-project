package com.depscanner.vulnservice.model.data.Response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectResponse {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private String projectType;
}
