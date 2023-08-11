package com.depscanner.projectservice.model.data.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequest {
    @NotBlank(message = "Name must not be blank or null")
    private String name;

    @NotBlank(message = "Description must not be blank or null")
    private String description;

    @NotNull(message = "isDailyScanned must not be null")
    private boolean isDailyScanned;

    @NotNull(message = "isWeeklyScanned must not be null")
    private boolean isWeeklyScanned;

    @NotEmpty(message = "Dependencies required to create a project")
    private List<DependencyRequest> dependencies;
}
