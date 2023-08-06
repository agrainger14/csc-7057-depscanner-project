package com.depscanner.projectservice.model.data.response;

import com.depscanner.projectservice.model.enumeration.ProjectType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectResponse {
    private String id;
    private String name;
    private String description;
    private ProjectType projectType;
}
