package com.depscanner.vulnservice.model.data.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DependencyRequest {
    @NotBlank(message = "Name must not be blank or null")
    private String name;

    @NotEmpty(message = "Version must not be empty")
    private String version;

    @NotNull(message = "System is required")
    private String system;
}
