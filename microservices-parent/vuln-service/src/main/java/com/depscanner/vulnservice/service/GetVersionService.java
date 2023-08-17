package com.depscanner.vulnservice.service;

import com.depscanner.vulnservice.depsdev.ApiHelper;
import com.depscanner.vulnservice.exception.NoDependencyInformationException;
import com.depscanner.vulnservice.mapper.Mapper;
import com.depscanner.vulnservice.model.data.Request.DependencyRequest;
import com.depscanner.vulnservice.model.data.Response.VulnCheckResponse;
import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryKeyDto;
import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryResponse;
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
        return fetchVersionData(system, dependencyName, version);
    }

    public VersionsResponseDto fetchVersionData(String system, String name, String version) {
        String getVersionUrl = ApiHelper.buildApiUrl(ApiHelper.GET_VERSION_URL, system, ApiHelper.percentEncodeParam(name), version);

        VersionsResponseDto responseDto =
                Optional.ofNullable(ApiHelper.makeApiRequest(getVersionUrl, VersionsResponseDto.class))
                        .orElseThrow(() -> new NoDependencyInformationException("No dependency version details available"));

        addDependencyData(responseDto);
        return responseDto;
    }

    public List<VulnCheckResponse> checkIfDependenciesVulnerable(List<DependencyRequest> dependencyRequestList) {
        List<VulnCheckResponse> versionsResponseDtoList = new LinkedList<>();

        for (DependencyRequest dependencyRequest : dependencyRequestList) {
            String name = dependencyRequest.getName();
            String system = dependencyRequest.getSystem();
            String version = dependencyRequest.getVersion();

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
            return Collections.emptyList();
        }

        return versionsResponseDtoList;
    }

    public void addDependencyData(VersionsResponseDto versionsResponseDto) {
        Optional<Version> dependencyVersionOptional = versionRepository.findByDependency_NameAndDependency_System_SystemAndVersion
                (versionsResponseDto.getVersionKey().getName(),
                        versionsResponseDto.getVersionKey().getSystem(),
                            versionsResponseDto.getVersionKey().getVersion());

        if (dependencyVersionOptional.isPresent()) {
            checkVersionDetail(dependencyVersionOptional.get(), versionsResponseDto);
            checkAdvisoryKeys(dependencyVersionOptional.get(), versionsResponseDto);
        } else {
            createDependencyVersion(versionsResponseDto);
        }
    }

    public void checkVersionDetail(Version version, VersionsResponseDto versionsResponseDto) {
        boolean dataAdded = false;

        if (version.getVersionDetail() == null) {
            version.setVersionDetail(mapper.mapToVersionDetail(versionsResponseDto, version));
            dataAdded = true;
        }

        if (version.getLinks().isEmpty()) {
            version.setLinks(versionsResponseDto.getLinks()
                    .stream()
                    .map(mapper::mapToLinkEntity)
                    .collect(Collectors.toList()));
            dataAdded = true;
        }

        if (version.getLicenses().isEmpty()) {
            version.setLicenses(versionsResponseDto.getLicenses()
                    .stream()
                    .map(mapper::mapToLicenseEntity)
                    .collect(Collectors.toList()));
            dataAdded = true;
        }

        if (dataAdded) {
            versionRepository.save(version);
        }
    }

    public void checkAdvisoryKeys(Version version, VersionsResponseDto versionsResponseDto) {
        if (versionsResponseDto.getAdvisoryKeys().size() > version.getAdvisoryKeys().size()) {
            log.info("New Advisories for dependency : " + version.getDependency().getName() + " " + version.getVersion() + " adding to DB!");
            version.setAdvisoryKeys(versionsResponseDto.getAdvisoryKeys().stream()
                    .map(mapper::mapToAdvisoryKeyEntity)
                    .collect(Collectors.toList()));
            versionRepository.save(version);
            getAdvisoryData(versionsResponseDto.getAdvisoryKeys());
        }
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
                .relatedDependencies(new LinkedList<>())
                .edges(new LinkedList<>())
                .build();

        dependencyVersion.setVersionDetail(mapper.mapToVersionDetail(versionsResponseDto,dependencyVersion));
        versionRepository.save(dependencyVersion);

        if (versionsResponseDto.getAdvisoryKeys().size() > 0) {
            getAdvisoryData(versionsResponseDto.getAdvisoryKeys());
        }

        return dependencyVersion;
    }

    public void getAdvisoryData(List<AdvisoryKeyDto> advisoryKey) {
        for (AdvisoryKeyDto advisory : advisoryKey) {
            String getAdvisoryUrl = ApiHelper.buildApiUrl(ApiHelper.GET_ADVISORY_URL, advisory.getId());

            AdvisoryResponse responseDto = ApiHelper.makeApiRequest(getAdvisoryUrl, AdvisoryResponse.class);

            if (responseDto != null) {
                getAdvisoryService.createAdvisoryData(responseDto);
            }
        }
    }
}
