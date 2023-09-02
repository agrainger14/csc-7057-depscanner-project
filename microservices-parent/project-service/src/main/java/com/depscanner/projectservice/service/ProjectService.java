package com.depscanner.projectservice.service;

import brave.Span;
import brave.Tracer;
import com.depscanner.projectservice.event.VulnScanEvent;
import com.depscanner.projectservice.exception.NoDependencyByIdException;
import com.depscanner.projectservice.exception.NoProjectByIdException;
import com.depscanner.projectservice.exception.NoUserProjectsException;
import com.depscanner.projectservice.exception.UserNotAuthorisedException;
import com.depscanner.projectservice.model.data.dto.DependencyDto;
import com.depscanner.projectservice.model.data.request.ProjectRequest;
import com.depscanner.projectservice.model.data.request.ScanUpdateRequest;
import com.depscanner.projectservice.model.data.response.*;
import com.depscanner.projectservice.model.entity.DependencyEntity;
import com.depscanner.projectservice.model.entity.ProjectEntity;
import com.depscanner.projectservice.model.enumeration.ProjectType;
import com.depscanner.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for handling user projects.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProjectService {
    private final AuthService authService;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final WebClient.Builder webClient;
    private final Tracer tracer;

    private final KafkaTemplate<String, VulnScanEvent> kafkaTemplate;

    /**
     * Retrieves a list of projects associated with the given user's email.
     * @return A list of ProjectResponse objects representing the user's projects and their dependencies.
     * @throws NoUserProjectsException If no projects are found for the given user's email.
     */
    public List<UserProjectResponse> readAllUserProjects() {
        List<ProjectEntity> projectEntityList = projectRepository.findAllByEmail(authService.getAuthEmail());

        if (projectEntityList.isEmpty()) {
            throw new NoUserProjectsException("No projects found for user");
        }

        List<UserProjectResponse> userProjectsResponses = projectEntityList.stream()
                .map(userProject -> UserProjectResponse.builder()
                        .id(userProject.getId())
                        .name(userProject.getName())
                        .createdAt(userProject.getCreatedAt())
                        .description(userProject.getDescription())
                        .projectType(userProject.getProjectType())
                        .isDailyScanned(userProject.isDailyScanned())
                        .isWeeklyScanned(userProject.isWeeklyScanned())
                        .projectDependenciesCount(userProject.getProjectDependenciesCount())
                        .dependencies(userProject.getDependencies()
                                .stream().
                                map(dependencyEntity -> modelMapper.map(dependencyEntity, DependencyDto.class))
                                .toList())
                        .build())
                .toList();

        userProjectsResponses.forEach(this::vulnerableDependencyCount);
        return userProjectsResponses;
    }

    /**
     * Checks the vulnerability of dependencies in the provided projects.
     * Updates the 'vulnerable' flag and sets the count of vulnerable dependencies in each project.
     * This method interacts with the vulnerability service to obtain up-to-date data on the vulnerability
     * status of the dependencies in the users projects.
     *
     * @param userProject The ProjectResponse objects to check for vulnerability.
     */
    private void vulnerableDependencyCount(UserProjectResponse userProject) {
        int vulnerableDependencyCount = 0;

        //convert the users projects dependencies to a list to make the API call to vuln-service
        List<DependencyDto> dependencyRequestList = userProject.getDependencies()
                .stream()
                .toList();

        Span vulnServiceCheckLookup = tracer.nextSpan().name("VulnServiceCheckLookup");

        //data is posted in a request body to prevent repeat API calls to the vuln-service
        try (Tracer.SpanInScope ignored = tracer.withSpanInScope(vulnServiceCheckLookup.start())) {
            DependencyResponse[] dependencyResponseArray = webClient.build().post()
                    .uri("http://vuln-service/check")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dependencyRequestList)
                    .retrieve()
                    .bodyToMono(DependencyResponse[].class)
                    .block();

            //create map of vulnerable dependency keys (with isDataAvailable flag)
            Map<String, Boolean> vulnerableDependenciesMap = Arrays.stream(Objects.requireNonNull(dependencyResponseArray))
                .collect(Collectors.toMap(
                        response -> response.getName() + response.getVersion() + response.getSystem(),
                        DependencyResponse::getIsDataAvailable)
                );

            for (DependencyDto dependency : dependencyRequestList) {
                // The 'id' is the name, version, and system of the dependency
                String key = dependency.getName() + dependency.getVersion() + dependency.getSystem();
                Boolean isDataAvailable = vulnerableDependenciesMap.get(key);

                if (isDataAvailable != null && isDataAvailable) {
                    //if present in the map from the response, the dependency is vulnerable.
                    dependency.setIsVulnerable(true);
                    vulnerableDependencyCount++;
                } else if (isDataAvailable != null) {
                    //if the key is present, but the value is false there is no information available, set to null.
                    dependency.setIsVulnerable(null);
                } else {
                    //if the key is present in the map, set to false as the dependency is not vulnerable.
                    dependency.setIsVulnerable(false);
                }
            }
            // After all dependencies are checked, set the vulnerable dependency count
            userProject.setVulnerableDependenciesCount(vulnerableDependencyCount);
        } finally {
            vulnServiceCheckLookup.finish();
        }
    }

    /**
     * Retrieves project details by its ID, associated with the users email.
     *
     * @param projectId The ID of the project to retrieve.
     * @return The ProjectResponse object representing the project details.
     * @throws NoProjectByIdException If no project is found with the given ID.
     * @throws UserNotAuthorisedException If the user is not authorised to access this project.
     */
    public UserProjectResponse readProjectById(String projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoProjectByIdException("Project by that ID does not exist!"));

        emailAuthorisationCheck(project);
        UserProjectResponse userProjectResponse = modelMapper.map(project, UserProjectResponse.class);
        vulnerableDependencyCount(userProjectResponse);

        return userProjectResponse;
    }

    /**
     * Creates a new user project with the provided details and associated dependencies.
     *
     * @param projectRequest The ProjectRequest object containing project details and dependencies.
     * @return The ProjectResponse object representing the newly created project.
     */
    public UserProjectResponse createUserProject(ProjectRequest projectRequest) {
        // get user email from auth realm
        String userEmail = authService.getAuthEmail();

        // model mapper prevents unneeded map methods
        ProjectEntity project = modelMapper.map(projectRequest, ProjectEntity.class);
        project.setEmail(userEmail);
        project.setProjectType(ProjectType.fromSystem(projectRequest.getDependencies().get(0).getSystem()));
        project.setProjectDependenciesCount(projectRequest.getDependencies().size());

        // set dependencies, creates unique ID for each dependency
        project.setDependencies(projectRequest.getDependencies()
                .stream()
                .map(dependency -> {
                    DependencyEntity dependencyEntity = modelMapper.map(dependency, DependencyEntity.class);
                    dependencyEntity.setId(UUID.randomUUID().toString());
                    return dependencyEntity;
                })
                .toList());

        // save project to db
        projectRepository.save(project);

        // null check to prevent worst case null pointer exception, then send a kafka event to scan against Deps.dev API
        if (project.getDependencies() != null) {
            List<DependencyDto> dependencyList = project.getDependencies()
                    .stream()
                    .map(dependency -> modelMapper.map(dependency, DependencyDto.class))
                    .toList();
            VulnScanEvent vulnScanEvent = new VulnScanEvent(userEmail, modelMapper.map(project, ProjectResponse.class), dependencyList);
            kafkaTemplate.send("project-vuln-scan-topic", vulnScanEvent);
        }

        // return with project successfully created
        UserProjectResponse userProjectResponse = modelMapper.map(project, UserProjectResponse.class);
        vulnerableDependencyCount(userProjectResponse);
        return userProjectResponse;
    }


    /**
     * Updates the scanning schedule for a project.
     *
     * @param projectId         The ID of the project to update.
     * @param scanUpdateRequest The request containing updated scan configuration.
     * @return The response indicating the updated scan configuration for the project.
     * @throws NoUserProjectsException If no project is found with the provided ID.
     */
    public ScanUpdateResponse updateProjectScheduledScan(String projectId, ScanUpdateRequest scanUpdateRequest) {
        Optional<ProjectEntity> userProjectOptional = projectRepository.findById(projectId);
        boolean newWeeklyScanned = scanUpdateRequest.isWeeklyScanned();
        boolean newDailyScanned = scanUpdateRequest.isDailyScanned();

        if (userProjectOptional.isEmpty()) {
            throw new NoUserProjectsException("No project found by that ID");
        }

        ProjectEntity userProject = userProjectOptional.get();

        if (newWeeklyScanned) {
            newDailyScanned = false;
        }

        userProject.setWeeklyScanned(newWeeklyScanned);
        userProject.setDailyScanned(newDailyScanned);

        projectRepository.save(userProject);
        return modelMapper.map(userProject, ScanUpdateResponse.class);
    }

    /**
     * Deletes a project dependency associated with the given user's email.
     *
     * @param projectId     The ID of the project containing the dependency to delete.
     * @param dependencyId  The ID of the dependency to delete.
     * @return The DeleteResponse object indicating the success of the deletion operation.
     * @throws NoProjectByIdException        If no project is found with the given ID.
     * @throws NoDependencyByIdException     If no dependency is found with the given ID.
     * @throws UserNotAuthorisedException    If the user is not authorised to access this project.
     */
    public DeleteResponse deleteProjectDependency(String projectId, String dependencyId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoProjectByIdException("Project by that ID does not exist!") );

        emailAuthorisationCheck(project);

        List<DependencyEntity> dependencyEntities = project.getDependencies();

        DependencyEntity dependencyToRemove = dependencyEntities.stream()
                .filter(dependency -> dependency.getId().equals(dependencyId))
                .findFirst()
                .orElseThrow(() -> new NoDependencyByIdException("Dependency by that ID does not exist!"));

        dependencyEntities.remove(dependencyToRemove);
        projectRepository.save(project);

        return DeleteResponse.builder()
                .success(true)
                .message("Dependency successfully removed from project")
                .build();
    }

    /**
     * Deletes a user project associated with the given user's email.
     *
     * @param projectId The ID of the project to delete.
     * @return The DeleteResponse object indicating the success of the deletion operation.
     * @throws NoProjectByIdException     If no project is found with the given ID.
     * @throws UserNotAuthorisedException If the user is not authorised to access this project.
     */
    public DeleteResponse deleteUserProject(String projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoProjectByIdException("Project by that ID does not exist!"));

        emailAuthorisationCheck(project);
        projectRepository.deleteById(projectId);

        return DeleteResponse.builder()
                .success(true)
                .message("Project successfully deleted")
                .build();
    }

    /**
     * Checks whether the given user is authorised to access the specified project.
     *
     * @param project   The ProjectEntity representing the project.
     * @throws UserNotAuthorisedException If the user is not authorized to access this project.
     */
    public void emailAuthorisationCheck(ProjectEntity project) {
        if (!project.getEmail().equals(authService.getAuthEmail())) {
            throw new UserNotAuthorisedException("User is not authorised to access this project!");
        }
    }
}
