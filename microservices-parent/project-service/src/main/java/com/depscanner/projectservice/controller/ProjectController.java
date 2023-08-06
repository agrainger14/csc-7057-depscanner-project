package com.depscanner.projectservice.controller;

import com.depscanner.projectservice.model.data.dto.DependencyDto;
import com.depscanner.projectservice.model.data.response.DeleteResponse;
import com.depscanner.projectservice.model.data.response.ProjectResponse;
import com.depscanner.projectservice.model.data.request.DependencyRequest;
import com.depscanner.projectservice.model.data.request.ProjectRequest;
import com.depscanner.projectservice.model.data.response.UserProjectResponse;
import com.depscanner.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/user")
    public ResponseEntity<UserProjectResponse> createProject(@RequestBody @Valid ProjectRequest projectRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createUserProject(projectRequest));
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserProjectResponse>> getUserProjects() {
        List<UserProjectResponse> userProjectResponses = projectService.readAllUserProjects();

        if (userProjectResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userProjectResponses);
    }

    @GetMapping("/id/{projectId}")
    public ResponseEntity<UserProjectResponse> getProjectDetails(@PathVariable("projectId") String projectId) {
        if (projectId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(projectService.readProjectById(projectId));
    }

    @PatchMapping("/id/{projectId}/dependency")
    public ResponseEntity<ProjectResponse> updateProjectDependencies(@PathVariable("projectId") String projectId,
                                                                     @RequestBody @Valid List<DependencyRequest> dependencies) {
        if (dependencies.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(projectService.updateProjectDependencies(projectId, dependencies));
    }

    @PatchMapping("/id/{projectId}/dependency/{dependencyId}/{version}")
    public ResponseEntity<DependencyDto> updatedProjectDependencyVersion(@PathVariable("projectId") String projectId,
                                                                         @PathVariable("dependencyId") String dependencyId,
                                                                         @PathVariable("version") String version) {
        if (dependencyId.isEmpty() || projectId.isEmpty() || version.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(projectService.updateProjectDependencyVersion(projectId, dependencyId, version));
    }

    @DeleteMapping("/id/{projectId}")
    public ResponseEntity<DeleteResponse> deleteUserProject(@PathVariable("projectId") String projectId) {
        if (projectId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(projectService.deleteUserProject(projectId));
    }

    @DeleteMapping("/id/{projectId}/dependency/{dependencyId}")
    public ResponseEntity<DeleteResponse> deleteProjectDependencyId(@PathVariable("projectId") String projectId,
                                                                    @PathVariable("dependencyId") String dependencyId) {
        if (dependencyId.isEmpty() || projectId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(projectService.deleteProjectDependency(projectId, dependencyId));
    }
}
