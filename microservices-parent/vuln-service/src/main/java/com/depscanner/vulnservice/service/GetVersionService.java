package com.depscanner.vulnservice.service;

import com.depscanner.vulnservice.depsdev.ApiHelper;
import com.depscanner.vulnservice.exception.NoDependencyInformationException;
import com.depscanner.vulnservice.mapper.Mapper;
import com.depscanner.vulnservice.model.data.Request.DependencyRequest;
import com.depscanner.vulnservice.model.data.Response.VulnCheckResponse;
import com.depscanner.vulnservice.model.data.getVersion.VersionsResponseDto;
import com.depscanner.vulnservice.model.entity.*;
import com.depscanner.vulnservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GetVersionService {
    private final Mapper mapper;
    private final VersionRepository versionRepository;
    private final GetAdvisoryService getAdvisoryService;

    public VersionsResponseDto readDependencyVersionDetail(String system, String dependencyName, String version) {
        Optional<Version> dependencyVersionOptional = versionRepository
                .findByDependency_NameAndDependency_System_SystemAndVersion(dependencyName, system, version);

        if (dependencyVersionOptional.isPresent()) {
            return mapper.mapToDependencyResponse(dependencyVersionOptional.get());
        }

        return fetchVersionData(system, dependencyName, version);
    }

    public VersionsResponseDto fetchVersionData(String system, String name, String version) {
        String getVersionUrl = ApiHelper.buildApiUrl(ApiHelper.GET_VERSION_URL, system, ApiHelper.percentEncodeParam(name), version);

        VersionsResponseDto responseDto =
                Optional.ofNullable(ApiHelper.makeApiRequest(getVersionUrl, VersionsResponseDto.class))
                        .orElseThrow(() -> new NoDependencyInformationException("No dependency version details available"));

        Optional<Version> versionOptional = versionRepository.findByDependency_NameAndDependency_System_SystemAndVersion
                (responseDto.getVersionKey().getName(), responseDto.getVersionKey().getSystem(), responseDto.getVersionKey().getVersion());

        if (versionOptional.isPresent()) {
            return mapper.mapToDependencyResponse(versionOptional.get());
        }

        createDependencyVersion(responseDto);
        return responseDto;
    }

    public Set<VulnCheckResponse> checkIfDependenciesVulnerable(List<DependencyRequest> dependencyRequestList) {
        Set<VulnCheckResponse> versionsResponseDtoList = new HashSet<>();

        for (DependencyRequest dependencyRequest : dependencyRequestList) {
            final String name = dependencyRequest.getName();
            final String system = dependencyRequest.getSystem();
            final String version = dependencyRequest.getVersion();

            Optional<Version> dependencyVersionOptional = versionRepository
                    .findByDependency_NameAndDependency_System_SystemAndVersion(name, system, version);

            if (dependencyVersionOptional.isEmpty()) {
                versionsResponseDtoList.add(mapper.mapToVulnCheckResponse(name, system, version));
                continue;
            }

            Version dependencyVersion = dependencyVersionOptional.get();
            List<RelatedDependency> relatedDependencies = dependencyVersion.getRelatedDependencies();

            boolean vulnerableRelatedDependency = relatedDependencies.stream()
                    .anyMatch(relatedDependency -> relatedDependency.getVersion().getAdvisoryKeys().size() > 0);

            if (dependencyVersion.getAdvisoryKeys().size() > 0 || vulnerableRelatedDependency) {
                versionsResponseDtoList.add(mapper.mapToVulnCheckResponse(dependencyVersion));
            }
        }

        if (versionsResponseDtoList.isEmpty()) {
            return Collections.emptySet();
        }

        return versionsResponseDtoList;
    }

    public void addDependencyData(VersionsResponseDto versionsResponseDto) {
        Optional<Version> dependencyVersionOptional = versionRepository.findByDependency_NameAndDependency_System_SystemAndVersion
                (versionsResponseDto.getVersionKey().getName(),
                        versionsResponseDto.getVersionKey().getSystem(),
                            versionsResponseDto.getVersionKey().getVersion());

        if (dependencyVersionOptional.isPresent()) {
            updateAdvisoryKeys(dependencyVersionOptional.get(), versionsResponseDto);
        } else {
            createDependencyVersion(versionsResponseDto);
        }
    }

    public void updateAdvisoryKeys(Version version, VersionsResponseDto versionsResponseDto) {
        version.setAdvisoryKeys(versionsResponseDto.getAdvisoryKeys().stream()
                .map(mapper::mapToAdvisoryKeyEntity)
                .collect(Collectors.toList()));
        version.setLicenses(versionsResponseDto.getLicenses()
                .stream()
                .map(mapper::mapToLicenseEntity)
                .collect(Collectors.toList()));
        version.setLinks(versionsResponseDto.getLinks()
                .stream()
                .map(mapper::mapToLinkEntity)
                .collect(Collectors.toList()));
        version.setVersionDetail(mapper.mapToVersionDetail(versionsResponseDto,version));
        versionRepository.save(version);
    }

    public Version createDependencyVersion(VersionsResponseDto versionsResponseDto) {
        Version dependencyVersion = Version.builder()
                .dependency(mapper.mapToDependency(versionsResponseDto.getVersionKey().getName(), versionsResponseDto.getVersionKey().getSystem()))
                .version(versionsResponseDto.getVersionKey().getVersion())
                .licenses(versionsResponseDto.getLicenses()
                            .stream()
                            .map(mapper::mapToLicenseEntity)
                            .toList())
                .advisoryKeys(versionsResponseDto.getAdvisoryKeys()
                            .stream()
                            .map(mapper::mapToAdvisoryKeyEntity)
                            .toList())
                .links(versionsResponseDto.getLinks()
                            .stream()
                            .map(mapper::mapToLinkEntity)
                            .toList())
                .build();
            dependencyVersion.setVersionDetail(mapper.mapToVersionDetail(versionsResponseDto,dependencyVersion));
            versionRepository.save(dependencyVersion);

            if (versionsResponseDto.getAdvisoryKeys().size() > 0) {
                versionsResponseDto.getAdvisoryKeys()
                        .forEach(advisoryKeyDto -> getAdvisoryService.fetchAdvisoryData(advisoryKeyDto.getId()));
            }

            return dependencyVersion;
        }
    }
