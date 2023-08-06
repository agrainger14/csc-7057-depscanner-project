package com.depscanner.vulnservice.service;

import com.depscanner.vulnservice.depsdev.ApiHelper;
import com.depscanner.vulnservice.exception.NoDependencyInformationException;
import com.depscanner.vulnservice.mapper.Mapper;
import com.depscanner.vulnservice.model.data.Response.RelatedDependencyResponse;
import com.depscanner.vulnservice.model.data.getDependencies.DependencyGraphResponseDto;
import com.depscanner.vulnservice.model.data.getDependencies.EdgeDto;
import com.depscanner.vulnservice.model.data.getDependencies.NodeDto;
import com.depscanner.vulnservice.model.entity.*;
import com.depscanner.vulnservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetDependenciesService {
    private final Mapper mapper;
    private final VersionRepository versionRepository;
    private final RelatedDependencyRepository relatedDependencyRepository;
    private final EdgeRepository edgeRepository;

    private final GetVersionService getVersionService;

    public RelatedDependencyResponse readDependencyDependencies(String system, String name, String version) {
        Optional<Version> versionEntityOptional = versionRepository
                .findByDependency_NameAndDependency_System_SystemAndVersion(name, system, version);

        if (versionEntityOptional.isPresent() && versionEntityOptional.get().getRelatedDependencies().size() > 0) {
            return mapper.mapToRelatedDependencyResponse(versionEntityOptional.get());
        }

       return fetchDependencyDependenciesData(system, name, version);
    }

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

    @Transactional
    public void addDependencyDependencies(DependencyGraphResponseDto dependencyGraphResponseDto) {
        // First node is the dependency versionKey (SELF), remaining nodes are DIRECT/INDIRECT versionKeys
        final List<NodeDto> nodes = dependencyGraphResponseDto.getNodes();
        final NodeDto nodeDto = nodes.get(0);

        final String name = nodeDto.getVersionKey().getName();
        final String system = nodeDto.getVersionKey().getSystem();
        final String version = nodeDto.getVersionKey().getVersion();

        final Version versionEntity = mapper.mapToVersionEntity(name, system, version);

        if (versionEntity.getRelatedDependencies().isEmpty()) {
            createDependencyGraphEntity(dependencyGraphResponseDto, versionEntity);
        } else {
            updateDependencyGraphEntity(dependencyGraphResponseDto, versionEntity);
        }
    }

    public void createDependencyGraphEntity(DependencyGraphResponseDto dependencyGraphResponseDto, Version version) {
        List<RelatedDependency> relatedDependencies = version.getRelatedDependencies();
        List<Edge> edges = version.getEdges();

        relatedDependencies.addAll(dependencyGraphResponseDto.getNodes().stream()
                .map(mapper::mapToNodeEntity)
                .toList());

        relatedDependencyRepository.saveAll(relatedDependencies);

        edges.addAll(dependencyGraphResponseDto.getEdges().stream()
                .map(mapper::mapToEdgeEntity)
                .toList());

        edgeRepository.saveAll(edges);
    }

    public void updateDependencyGraphEntity(DependencyGraphResponseDto dependencyGraphResponseDto, Version version) {
        List<RelatedDependency> existingRelatedDependencies = version.getRelatedDependencies();
        List<Edge> existingEdges = version.getEdges();

        List<RelatedDependency> newRelatedDependencies = dependencyGraphResponseDto.getNodes().stream()
                .map(mapper::mapToNodeEntity)
                .toList();

        List<Edge> newEdges = dependencyGraphResponseDto.getEdges().stream()
                .map(mapper::mapToEdgeEntity)
                .toList();

        if (!existingRelatedDependencies.containsAll(newRelatedDependencies)) {
            existingRelatedDependencies.clear();
            relatedDependencyRepository.saveAll(newRelatedDependencies);
        }

        if (!existingEdges.containsAll(newEdges)) {
            existingEdges.clear();
            edgeRepository.saveAll(newEdges);
        }
    }
}


