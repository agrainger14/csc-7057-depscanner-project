package com.depscanner.projectservice.service;

import brave.Span;
import brave.Tracer;
import com.depscanner.projectservice.exception.*;
import com.depscanner.projectservice.model.data.dto.DependencyDto;
import com.depscanner.projectservice.model.data.request.DependencyRequest;
import com.depscanner.projectservice.model.data.request.ProjectRequest;
import com.depscanner.projectservice.model.data.response.DeleteResponse;
import com.depscanner.projectservice.model.data.response.DependencyResponse;
import com.depscanner.projectservice.model.data.response.ProjectResponse;
import com.depscanner.projectservice.event.VulnScanEvent;
import com.depscanner.projectservice.model.data.response.UserProjectResponse;
import com.depscanner.projectservice.model.enumeration.ProjectType;
import com.depscanner.projectservice.repository.ProjectRepository;
import com.depscanner.projectservice.model.entity.DependencyEntity;
import com.depscanner.projectservice.model.entity.ProjectEntity;
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
 * ProjectService is a service class responsible for managing user projects.
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
                        .projectDependenciesCount(userProject.getProjectDependenciesCount())
                        .dependencies(userProject.getDependencies()
                                .stream().
                                map(dependencyEntity -> modelMapper.map(dependencyEntity, DependencyDto.class))
                                .toList())
                        .build())
                .toList();
        vulnerableDependencyCount(userProjectsResponses);
        return userProjectsResponses;
    }

    /**
     * Checks the vulnerability of dependencies in the provided projects.
     * Updates the 'vulnerable' flag and sets the count of vulnerable dependencies in each project.
     * This method interacts with the vulnerability service to obtain up-to-date data on the vulnerability
     * status of the dependencies in the users projects.
     *
     * @param userProjects The list of ProjectResponse objects to check for vulnerability.
     */
    private void vulnerableDependencyCount(List<UserProjectResponse> userProjects) {
        //convert the users projects dependencies to a list to make the API call to vuln-service
        List<DependencyDto> dependencyRequestList = userProjects.stream()
                .map(UserProjectResponse::getDependencies)
                .flatMap(List::stream)
                .toList();

        Span vulnServiceCheckLookup = tracer.nextSpan().name("VulnServiceCheckLookup");

        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(vulnServiceCheckLookup.start())) {
            //data is posted in a request body to prevent repeat API calls to the vuln-service
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
                            DependencyResponse::getIsDataAvailable
                    ));

            //loop through projectResponses to check all dependencies
            for (UserProjectResponse projectResponse : userProjects) {
                int vulnerableDependencyCount = 0;
                List<DependencyDto> dependencies = projectResponse.getDependencies();

                for (DependencyDto dependency : dependencies) {
                    //the 'id' is the name, version and system of the dependency.
                    String key = dependency.getName() + dependency.getVersion() + dependency.getSystem();
                    Boolean isDataAvailable = vulnerableDependenciesMap.get(key);

                    if (isDataAvailable != null && isDataAvailable) {
                        //if present in the map from the response, the dependency is vulnerable.
                        dependency.setIsVulnerable(true);
                        vulnerableDependencyCount++;
                    } else if (isDataAvailable != null && !isDataAvailable) {
                        //if the key is present, but the value is false there is no information available, set to null.
                        dependency.setIsVulnerable(null);
                    } else {
                        //if the key is present in the map, set to false as the dependency is not vulnerable.
                        dependency.setIsVulnerable(false);
                    }
                }
                //after all dependencies checked, set the vulnerable dependency count.
                projectResponse.setVulnerableDependenciesCount(vulnerableDependencyCount);
            }
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
        vulnerableDependencyCount(Collections.singletonList(userProjectResponse));

        return userProjectResponse;
    }

    /**
     * Creates a new user project with the provided details and associated dependencies.
     *
     * @param projectRequest The ProjectRequest object containing project details and dependencies.
     * @return The ProjectResponse object representing the newly created project.
     */
    public UserProjectResponse createUserProject(ProjectRequest projectRequest) {
        //model mapper prevents some unneeded map methods
        ProjectEntity project = modelMapper.map(projectRequest, ProjectEntity.class);

        project.setEmail(authService.getAuthEmail());
        project.setProjectType(ProjectType.fromSystem(projectRequest.getDependencies().get(0).getSystem()));
        project.setProjectDependenciesCount(projectRequest.getDependencies().size());

        //set dependencies, creates unique ID for each dependency
        project.setDependencies(projectRequest.getDependencies()
                .stream()
                .map(dependency -> {
                    DependencyEntity dependencyEntity = modelMapper.map(dependency, DependencyEntity.class);
                    dependencyEntity.setId(UUID.randomUUID().toString());
                    return dependencyEntity;
                })
                .toList());

        //null check to prevent worst case null pointer exception, then send a kafka event to scan against Deps.dev API
        if (project.getDependencies() != null) {
            List<DependencyDto> dependencyList = project.getDependencies()
                    .stream()
                    .map(dependency -> modelMapper.map(dependency, DependencyDto.class))
                    .toList();

            VulnScanEvent vulnScanEvent = new VulnScanEvent(authService.getAuthEmail(), modelMapper.map(project, ProjectResponse.class), dependencyList);
            kafkaTemplate.send("project-vuln-scan-topic", vulnScanEvent);
        }

        //save project to db
        projectRepository.save(project);

        //return with project successfully created
        UserProjectResponse userProjectResponse = modelMapper.map(project, UserProjectResponse.class);
        vulnerableDependencyCount(Collections.singletonList(userProjectResponse));
        return userProjectResponse;
    }

    /**
     * Updates the version of a project dependency associated with the given user's email.
     *
     * @param projectId     The ID of the project containing the dependency to update.
     * @param dependencyId  The ID of the dependency to update.
     * @param version       The new version of the dependency.
     * @return The updated DependencyDto object representing the updated dependency.
     * @throws NoProjectByIdException        If no project is found with the given ID.
     * @throws NoDependencyByIdException     If no dependency is found with the given ID.
     * @throws UserNotAuthorisedException    If the user is not authorised to access this project.
     * @throws NoProjectDependenciesException If the project has no dependencies.
     */
    public DependencyDto updateProjectDependencyVersion(String projectId, String dependencyId, String version) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoProjectByIdException("Project by that ID does not exist!") );

        emailAuthorisationCheck(project);

        if (project.getDependencies().isEmpty()) {
            throw new NoProjectDependenciesException("No project dependencies exist!");
        }

        List<DependencyEntity> dependencyEntities = project.getDependencies();

        DependencyEntity dependencyToUpdate = dependencyEntities.stream()
                .filter(dependency -> dependency.getId().equals(dependencyId))
                .findFirst()
                .orElseThrow(() -> new NoDependencyByIdException("Dependency by that ID does not exist!"));

        dependencyToUpdate.setVersion(version);
        projectRepository.save(project);

        return modelMapper.map(dependencyToUpdate, DependencyDto.class);
    }

    /**
     * Updates the list of dependencies for a project associated with the given user's email.
     *
     * @param projectId     The ID of the project to update.
     * @param dependencies  The list of DependencyRequest objects representing the new dependencies.
     * @return The updated ProjectResponse object representing the project with new dependencies.
     * @throws NoProjectByIdException        If no project is found with the given ID.
     * @throws UserNotAuthorisedException    If the user is not authorised to access this project.
     * @throws NoProjectDependenciesException If the project has no dependencies.
     */
    public ProjectResponse updateProjectDependencies(String projectId, List<DependencyRequest> dependencies) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoProjectByIdException("Project by that ID does not exist!") );

        emailAuthorisationCheck(project);

        if (project.getDependencies().isEmpty()) {
            throw new NoProjectDependenciesException("No project dependencies exist!");
        }

        project.getDependencies().clear();
        project.setDependencies(dependencies
                .stream()
                .map(dependency -> {
                    DependencyEntity dependencyEntity = modelMapper.map(dependency, DependencyEntity.class);
                    dependencyEntity.setId(UUID.randomUUID().toString());
                    return dependencyEntity;
                })
                .toList());

        projectRepository.save(project);
        return modelMapper.map(project, ProjectResponse.class);
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
                .orElseThrow(() -> new NoProjectByIdException("Project by that ID does not exist!") );

        emailAuthorisationCheck(project);
        projectRepository.deleteById(projectId);

        return DeleteResponse.builder()
                .success(true)
                .message("Project successfully deleted")
                .build();
    }

    /**
     * Retrieves a list of all projects available in the database.
     *
     * @return A list of ProjectResponse objects representing all projects.
     */
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(project -> modelMapper.map(project, ProjectResponse.class))
                .toList();
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
