package com.depscanner.projectservice.model.data.request;

import com.depscanner.projectservice.model.enumeration.BuildToolType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Data class representing a request to define an open-source software dependency.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DependencyRequest {
    @NotBlank(message = "Name must not be blank or null")
    private String name;

    @NotEmpty(message = "Version must not be empty")
    private String version;

    @NotNull(message = "Build tool type is required")
    private BuildToolType system;

    @NotNull(message = "isDevDependency must be true or false")
    private Boolean isDevDependency;
}
