package com.depscanner.projectservice.controller;

import com.depscanner.projectservice.model.data.request.ProjectRequest;
import com.depscanner.projectservice.model.data.request.ScanUpdateRequest;
import com.depscanner.projectservice.model.data.response.DeleteResponse;
import com.depscanner.projectservice.model.data.response.ScanUpdateResponse;
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

/**
 * Controller class handling RESTful API endpoints related to project management and interactions.
 */
@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class ProjectController {

    private final ProjectService projectService;


    /**
     * Creates a new project associated with a user.
     *
     * @param projectRequest The request containing project details.
     * @return A ResponseEntity with the newly created user project response and HTTP status code 201 (Created).
     */
    @PostMapping("/user")
    public ResponseEntity<UserProjectResponse> createProject(@RequestBody @Valid ProjectRequest projectRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createUserProject(projectRequest));
    }

    /**
     * Retrieves a list of user projects.
     *
     * @return A ResponseEntity with a list of user project responses and HTTP status code 200 (OK), or HTTP status code 204 (No Content) if the list is empty.
     */
    @GetMapping("/user")
    public ResponseEntity<List<UserProjectResponse>> getUserProjects() {
        List<UserProjectResponse> userProjectResponses = projectService.readAllUserProjects();

        if (userProjectResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userProjectResponses);
    }

    /**
     * Retrieves project details by project ID.
     *
     * @param projectId The ID of the project to retrieve.
     * @return A ResponseEntity with the project details and HTTP status code 200 (OK), or HTTP status code 400 (Bad Request) if the projectId is empty.
     */
    @GetMapping("/id/{projectId}")
    public ResponseEntity<UserProjectResponse> getProjectDetails(@PathVariable("projectId") String projectId) {
        if (projectId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(projectService.readProjectById(projectId));
    }

    /**
     * Updates the scheduled scan configuration of a project.
     *
     * @param projectId       The ID of the project to update.
     * @param scanUpdateRequest The request containing the updated scan configuration.
     * @return A ResponseEntity with the scan update response and HTTP status code 200 (OK).
     */
    @PatchMapping("/id/{projectId}")
    public ResponseEntity<ScanUpdateResponse> updateProjectScheduledScan(@PathVariable("projectId") String projectId,
                                                                        @RequestBody @Valid ScanUpdateRequest scanUpdateRequest) {
        return ResponseEntity.ok(projectService.updateProjectScheduledScan(projectId, scanUpdateRequest));
    }

    /**
     * Deletes a user project by project ID.
     *
     * @param projectId The ID of the project to delete.
     * @return A ResponseEntity with the delete response and HTTP status code 200 (OK), or HTTP status code 400 (Bad Request) if the projectId is empty.
     */
    @DeleteMapping("/id/{projectId}")
    public ResponseEntity<DeleteResponse> deleteUserProject(@PathVariable("projectId") String projectId) {
        if (projectId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(projectService.deleteUserProject(projectId));
    }

    /**
     * Deletes a dependency of a project by project ID and dependency ID.
     *
     * @param projectId    The ID of the project.
     * @param dependencyId The ID of the dependency to delete.
     * @return A ResponseEntity with the delete response and HTTP status code 200 (OK), or HTTP status code 400 (Bad Request) if either the projectId or dependencyId is empty.
     */
    @DeleteMapping("/id/{projectId}/dependency/{dependencyId}")
    public ResponseEntity<DeleteResponse> deleteProjectDependencyId(@PathVariable("projectId") String projectId,
                                                                    @PathVariable("dependencyId") String dependencyId) {
        if (dependencyId.isEmpty() || projectId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(projectService.deleteProjectDependency(projectId, dependencyId));
    }
}
