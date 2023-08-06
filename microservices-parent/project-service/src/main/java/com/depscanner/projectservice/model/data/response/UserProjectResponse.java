package com.depscanner.projectservice.model.data.response;

import com.depscanner.projectservice.model.data.dto.DependencyDto;
import com.depscanner.projectservice.model.enumeration.ProjectType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProjectResponse {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private ProjectType projectType;
    private int projectDependenciesCount;
    private int vulnerableDependenciesCount;
    private List<DependencyDto> dependencies;
}
