package com.depscanner.projectservice.model.entity;

import com.depscanner.projectservice.model.enumeration.ProjectType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(value="project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProjectEntity {
    @Id
    private String id;

    private String email;
    private String name;
    private String description;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private ProjectType projectType;
    private int projectDependenciesCount;
    private List<DependencyEntity> dependencies;

}
