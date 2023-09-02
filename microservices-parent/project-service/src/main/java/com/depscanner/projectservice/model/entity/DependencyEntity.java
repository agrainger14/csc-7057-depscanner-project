package com.depscanner.projectservice.model.entity;

import com.depscanner.projectservice.model.enumeration.BuildToolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DependencyEntity {
    @Id
    private String id;

    private String name;
    private String version;
    private BuildToolType system;
    private Boolean isDevDependency;
}