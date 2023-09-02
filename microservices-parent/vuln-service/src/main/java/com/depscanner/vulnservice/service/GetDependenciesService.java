package com.depscanner.vulnservice.service;

import com.depscanner.vulnservice.depsdev.ApiHelper;
import com.depscanner.vulnservice.exception.NoDependencyInformationException;
import com.depscanner.vulnservice.mapper.Mapper;
import com.depscanner.vulnservice.model.data.Response.RelatedDependencyResponse;
import com.depscanner.vulnservice.model.data.getDependencies.DependencyGraphResponseDto;
import com.depscanner.vulnservice.model.data.getDependencies.NodeDto;
import com.depscanner.vulnservice.model.entity.Edge;
import com.depscanner.vulnservice.model.entity.RelatedDependency;
import com.depscanner.vulnservice.model.entity.Version;
import com.depscanner.vulnservice.repository.EdgeRepository;
import com.depscanner.vulnservice.repository.RelatedDependencyRepository;
import com.depscanner.vulnservice.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for managing dependency data, including reading and fetching related dependencies for an open-source dependency.
 * This service provides methods to read dependency dependencies by system, name, and version,
 * fetch dependency dependencies from the deps.dev API, and create or update dependency information in the database.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GetDependenciesService {
    private final Mapper mapper;
    private final VersionRepository versionRepository;
    private final RelatedDependencyRepository relatedDependencyRepository;
    private final EdgeRepository edgeRepository;

    private final GetVersionService getVersionService;

    /**
     * Reads dependency related dependencies by system, name, and version.
     *
     * @param system  The system of the dependency.
     * @param name    The name of the dependency.
     * @param version The version of the dependency.
     * @return A {@link RelatedDependencyResponse} containing related dependency information.
     * @throws NoDependencyInformationException if no dependency data is available.
     */
    public RelatedDependencyResponse readDependencyDependencies(String system, String name, String version) {
        Optional<Version> versionEntityOptional = versionRepository
                .findByDependency_NameAndDependency_System_SystemAndVersion(name, system, version);

        if (versionEntityOptional.isPresent() && versionEntityOptional.get().getRelatedDependencies().size() > 0) {
            return mapper.mapToRelatedDependencyResponse(versionEntityOptional.get());
        }

        return fetchDependencyDependenciesData(system, name, version);
    }

    /**
     * Fetches related dependencies from the deps.dev API by system, name, and version.
     *
     * @param system  The system of the dependency.
     * @param name    The name of the dependency.
     * @param version The version of the dependency.
     * @return A {@link RelatedDependencyResponse} containing the fetched related dependency information.
     * @throws NoDependencyInformationException if no dependency data is available.
     */
    public RelatedDependencyResponse fetchDependencyDependenciesData(String system, String name, String version) {
        final String getDependenciesUrl = ApiHelper.buildApiUrl
                (ApiHelper.GET_DEPENDENCY_URL, system, ApiHelper.percentEncodeParam(name), version);

        DependencyGraphResponseDto responseDto =
                Optional.ofNullable(ApiHelper.makeApiRequest(getDependenciesUrl, DependencyGraphResponseDto.class))
                        .orElseThrow(() -> new NoDependencyInformationException("No dependency data available"));

        responseDto.getNodes().forEach(dependency -> getVersionService.fetchVersionData(dependency.getVersionKey().getSystem(),
                dependency.getVersionKey().getName(), dependency.getVersionKey().getVersion()));

        addDependencyDependencies(responseDto);
        Version versionEntity = mapper.mapToVersionEntity(name, system, version);
        return mapper.mapToRelatedDependencyResponse(versionEntity);
    }

    /**
     * Adds related dependencies to the database based on the fetched data.
     *
     * @param dependencyGraphResponseDto The dependency graph response DTO containing related dependency information.
     */
    public void addDependencyDependencies(DependencyGraphResponseDto dependencyGraphResponseDto) {
        // First node is the dependency versionKey (SELF), remaining nodes are DIRECT/INDIRECT versionKeys
        List<NodeDto> nodes = dependencyGraphResponseDto.getNodes();
        NodeDto nodeDto = nodes.get(0);

        final String name = nodeDto.getVersionKey().getName();
        final String system = nodeDto.getVersionKey().getSystem();
        final String version = nodeDto.getVersionKey().getVersion();

        Version versionEntity = mapper.mapToVersionEntity(name, system, version);

        if (versionEntity.getRelatedDependencies().isEmpty()) {
            createDependencyGraphEntity(dependencyGraphResponseDto, versionEntity);
        }
    }

    /**
     * Creates related dependency entities in the database based on the fetched data.
     *
     * @param dependencyGraphResponseDto The dependency graph response DTO containing related dependency information.
     * @param version                    The version entity to associate with the dependency dependencies.
     */
    public void createDependencyGraphEntity(DependencyGraphResponseDto dependencyGraphResponseDto, Version version) {
        List<RelatedDependency> relatedDependencies = version.getRelatedDependencies();
        List<Edge> edges = version.getEdges();

        relatedDependencies.addAll(dependencyGraphResponseDto.getNodes().stream()
                .map(mapper::mapToRelatedDependencyEntity)
                .toList());
        relatedDependencyRepository.saveAll(relatedDependencies);

        edges.addAll(dependencyGraphResponseDto.getEdges().stream()
                .map(mapper::mapToEdgeEntity)
                .toList());
        edgeRepository.saveAll(edges);
    }
}