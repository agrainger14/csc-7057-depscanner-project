package com.depscanner.vulnservice.event;

import com.depscanner.vulnservice.model.data.Response.ProjectResponse;
import com.depscanner.vulnservice.model.data.getVersion.DependencyDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VulnScanEvent {
    private String userEmail;
    private ProjectResponse projectResponse;
    private List<DependencyDto> dependencies;
}