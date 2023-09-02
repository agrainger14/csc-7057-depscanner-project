package com.depscanner.vulnservice.service;

import com.depscanner.vulnservice.depsdev.ApiHelper;
import com.depscanner.vulnservice.exception.NoDependencyInformationException;
import com.depscanner.vulnservice.mapper.Mapper;
import com.depscanner.vulnservice.model.data.Request.DependencyRequest;
import com.depscanner.vulnservice.model.data.Response.VulnCheckResponse;
import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryKeyDto;
import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryResponse;
import com.depscanner.vulnservice.model.data.getVersion.VersionsResponseDto;
import com.depscanner.vulnservice.model.entity.RelatedDependency;
import com.depscanner.vulnservice.model.entity.Version;
import com.depscanner.vulnservice.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing version and dependency data, including reading and fetching version details from the deps.dev API,
 * checking for vulnerabilities, and creating or updating version and dependency data in the database.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GetVersionService {
    private final Mapper mapper;
    private final VersionRepository versionRepository;
    private final GetAdvisoryService getAdvisoryService;

    /**
     * Reads version details of a dependency by system, dependency name and version.
     *
     * @param system         The system of the dependency.
     * @param dependencyName The name of the dependency.
     * @param version        The version of the dependency.
     * @return A {@link VersionsResponseDto} containing version details.
     * @throws NoDependencyInformationException if no dependency version details are available.
     */
    public VersionsResponseDto readDependencyVersionDetail(String system, String dependencyName, String version) {
        return fetchVersionData(system, dependencyName, version);
    }

    /**
     * Fetches version details of a dependency from the deps.dev API by system, dependency name, and version.
     *
     * @param system   The system of the dependency.
     * @param name     The name of the dependency.
     * @param version  The version of the dependency.
     * @return A {@link VersionsResponseDto} containing the fetched version details.
     * @throws NoDependencyInformationException if no dependency version details are available.
     */
    public VersionsResponseDto fetchVersionData(String system, String name, String version) {
        String getVersionUrl = ApiHelper.buildApiUrl(ApiHelper.GET_VERSION_URL, system, ApiHelper.percentEncodeParam(name), version);

        VersionsResponseDto responseDto =
                Optional.ofNullable(ApiHelper.makeApiRequest(getVersionUrl, VersionsResponseDto.class))
                        .orElseThrow(() -> new NoDependencyInformationException("No dependency version details available"));

        addDependencyData(responseDto);
        return responseDto;
    }

    /**
     * Adds dependency data to the database if it doesn't exist and updates details if necessary.
     *
     * @param versionsResponseDto The version response DTO containing version and dependency data.
     */
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

    /**
     * Checks if dependencies are vulnerable based on advisory data.
     *
     * @param dependencyRequestList A list of {@link DependencyRequest} objects specifying dependencies to check.
     * @return A list of {@link VulnCheckResponse} objects indicating vulnerability status.
     */
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

    /**
     * Checks and updates version details, links, licenses and advisory keys for an existing dependency version.
     *
     * @param version             The existing version entity to update.
     * @param versionsResponseDto The version response DTO containing updated version data.
     */
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

    /**
     * Checks and updates advisory keys for an existing dependency version.
     *
     * @param version             The existing version entity to update.
     * @param versionsResponseDto The version response DTO containing advisory key data.
     */
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

    /**
     * Creates a new dependency version entity in the database.
     *
     * @param versionsResponseDto The version response DTO containing version and dependency data to create.
     */
    public void createDependencyVersion(VersionsResponseDto versionsResponseDto) {
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

    }

    /**
     * Retrieves advisory data for a list of advisory keys and updates the database with the received data.
     *
     * @param advisoryKey A list of {@link AdvisoryKeyDto} objects representing advisory keys to fetch data for.
     */
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
