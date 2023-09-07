package com.depscanner.vulnservice.mapper;

import com.depscanner.vulnservice.model.data.Response.DependencyResponse;
import com.depscanner.vulnservice.model.data.Response.RelatedDependencyResponse;
import com.depscanner.vulnservice.model.data.Response.VulnCheckResponse;
import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryKeyDto;
import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryResponse;
import com.depscanner.vulnservice.model.data.getDependencies.EdgeDto;
import com.depscanner.vulnservice.model.data.getDependencies.NodeDto;
import com.depscanner.vulnservice.model.data.getPackage.PackageKeyDto;
import com.depscanner.vulnservice.model.data.getPackage.PackageResponseDto;
import com.depscanner.vulnservice.model.data.getPackage.VersionDto;
import com.depscanner.vulnservice.model.data.getPackage.VersionKeyDto;
import com.depscanner.vulnservice.model.data.getVersion.DependencyDto;
import com.depscanner.vulnservice.model.data.getVersion.LinkDto;
import com.depscanner.vulnservice.model.data.getVersion.VersionsResponseDto;
import com.depscanner.vulnservice.model.entity.*;
import com.depscanner.vulnservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This class serves as a Mapper for converting DTOs to Entity objects
 * and vice versa. It contains methods for mapping various data structures within the vuln-service DB schema.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class Mapper {
    private final AdvisoryDetailRepository advisoryDetailRepository;
    private final SystemRepository systemRepository;
    private final DependencyRepository dependencyRepository;
    private final VersionRepository versionRepository;
    private final AdvisoryKeyRepository advisoryKeyRepository;
    private final LinkRepository linkRepository;
    private final LicenseRepository licenseRepository;
    private final VersionDetailRepository versionDetailRepository;

    /**
     * Maps an EdgeDto object to an Edge entity.
     *
     * @param edge The EdgeDto to be mapped.
     * @return The corresponding Edge entity.
     */
    public Edge mapToEdgeEntity(EdgeDto edge) {
        return Edge.builder()
                .fromNode(edge.getFromNode())
                .toNode(edge.getToNode())
                .requirement(edge.getRequirement())
                .build();
    }

    /**
     * Maps a NodeDto to a RelatedDependency entity.
     *
     * @param nodeDto The NodeDto to be mapped.
     * @return The corresponding RelatedDependency entity.
     */
    public RelatedDependency mapToRelatedDependencyEntity(NodeDto nodeDto) {
        return RelatedDependency.builder()
                .bundled(nodeDto.getBundled())
                .errors(nodeDto.getErrors())
                .relation(nodeDto.getRelation())
                .version(mapToVersionEntity(nodeDto.getVersionKey().getName(), nodeDto.getVersionKey().getSystem(), nodeDto.getVersionKey().getVersion()))
                .build();
    }

    /**
     * Maps the given name, system, and version to a Version entity.
     *
     * @param name    The name of the dependency.
     * @param system  The system of the dependency.
     * @param version The version of the dependency.
     * @return The corresponding Version entity.
     */
    public Version mapToVersionEntity(String name, String system, String version) {
        Optional<Version> versionOptional = versionRepository
                .findByDependency_NameAndDependency_System_SystemAndVersion(name, system, version);

        if (versionOptional.isPresent()) {
            return versionOptional.get();
        }

        Version versionEntity = Version.builder()
                .dependency(mapToDependency(name, system))
                .version(version)
                .build();
        versionRepository.save(versionEntity);
        return versionEntity;
    }

    /**
     * Maps the given name and system to a Dependency entity.
     *
     * @param name   The name of the dependency.
     * @param system The system of the dependency.
     * @return The corresponding Dependency entity.
     */
    public Dependency mapToDependency(String name, String system) {
        Optional<Dependency> dependencyOptional = dependencyRepository.
                findByNameAndSystem_System(name, system);

        if (dependencyOptional.isPresent()) {
            return dependencyOptional.get();
        }

        Dependency dependency = Dependency.builder()
                .name(name)
                .system(mapToSystemEntity(system))
                .build();
        dependencyRepository.save(dependency);
        return dependency;
    }

    /**
     * Maps the given system to a SystemEntity.
     *
     * @param system The system name.
     * @return The corresponding SystemEntity.
     */
    public SystemEntity mapToSystemEntity(String system) {
        Optional<SystemEntity> systemEntityOptional = systemRepository.findBySystem(system);

        if (systemEntityOptional.isPresent()) {
            return systemEntityOptional.get();
        }

        SystemEntity systemEntity = SystemEntity.builder()
                .system(system)
                .build();
        systemRepository.save(systemEntity);
        return systemEntity;
    }

    /**
     * Maps a Version to a RelatedDependencyResponse.
     *
     * @param version The Version entity to be mapped.
     * @return The corresponding RelatedDependencyResponse.
     */
    public RelatedDependencyResponse mapToRelatedDependencyResponse(Version version) {
        return RelatedDependencyResponse.builder()
                .dependency(version.getRelatedDependencies().stream()
                        .map(this::mapToDependencyResponse).toList())
                .edges(version.getEdges().stream()
                        .map(this::mapToEdgeDto).toList())
                .build();
    }

    /**
     * Maps a RelatedDependency to a DependencyResponse.
     *
     * @param relatedDependency The RelatedDependency entity to be mapped.
     * @return The corresponding DependencyResponse.
     */
    public DependencyResponse mapToDependencyResponse(RelatedDependency relatedDependency) {
        return DependencyResponse.builder()
                .versionKey(mapToVersionKey(relatedDependency.getVersion().getDependency(), relatedDependency.getVersion().getVersion()))
                .bundled(relatedDependency.getBundled())
                .relation(relatedDependency.getRelation())
                .advisoryDetail(relatedDependency.getVersion().getAdvisoryKeys()
                        .stream()
                        .map(this::mapToAdvisoryResponse)
                        .toList())
                .licenses(relatedDependency.getVersion().getLicenses()
                        .stream()
                        .map(this::mapToLicenseResponse)
                        .toList())
                .errors(relatedDependency.getErrors())
                .links(relatedDependency.getVersion().getLinks()
                        .stream()
                        .map(this::mapToLinkResponse)
                        .toList())
                .publishedAt(relatedDependency.getVersion().getVersionDetail().getPublishedAt())
                .build();
    }

    /**
     * Maps a Link to a LinkDto.
     *
     * @param link The Link entity to be mapped.
     * @return The corresponding LinkDto.
     */
    public LinkDto mapToLinkResponse(Link link) {
        return LinkDto.builder()
                .url(link.getUrl())
                .label(link.getLabel())
                .build();
    }

    /**
     * Maps a License to a String representation of the license.
     *
     * @param license The License entity to be mapped.
     * @return The String representation of the license.
     */
    public String mapToLicenseResponse(License license) {
        return license.getLicense();
    }

    /**
     * Maps an Edge to an EdgeDto.
     *
     * @param edge The Edge entity to be mapped.
     * @return The corresponding EdgeDto.
     */
    public EdgeDto mapToEdgeDto(Edge edge) {
        return EdgeDto.builder()
                .toNode(edge.getToNode())
                .fromNode(edge.getFromNode())
                .requirement(edge.getRequirement())
                .build();
    }

    /**
     * Maps a Dependency and version to a DependencyDto.
     *
     * @param dependency The Dependency entity to be mapped.
     * @param version    The version of the dependency.
     * @return The corresponding DependencyDto.
     */
    public DependencyDto mapToVersionKey(Dependency dependency, String version) {
        return DependencyDto.builder()
                .name(dependency.getName())
                .system(dependency.getSystem().getSystem())
                .version(version)
                .build();
    }

    /**
     * Maps an AdvisoryKey to an AdvisoryResponse.
     *
     * @param advisoryKey The AdvisoryKey entity to be mapped.
     * @return The corresponding AdvisoryResponse.
     */
    public AdvisoryResponse mapToAdvisoryResponse(AdvisoryKey advisoryKey) {
        return AdvisoryResponse.builder()
                .advisoryKey(mapToAdvisoryKey(advisoryKey.getAdvisoryId()))
                .url(advisoryKey.getAdvisoryDetail().getUrl())
                .title(advisoryKey.getAdvisoryDetail().getTitle())
                .aliases(advisoryKey.getAdvisoryDetail().getAliases())
                .cvss3Score(advisoryKey.getAdvisoryDetail().getCvss3Score())
                .cvss3Vector(advisoryKey.getAdvisoryDetail().getCvss3Vector())
                .build();
    }

    /**
     * Maps an AdvisoryId to an AdvisoryKeyDto.
     *
     * @param advisoryId The AdvisoryId to be mapped.
     * @return The corresponding AdvisoryKeyDto.
     */
    public AdvisoryKeyDto mapToAdvisoryKey(String advisoryId) {
        return AdvisoryKeyDto.builder()
                .id(advisoryId)
                .build();
    }

    /**
     * Maps an AdvisoryResponse to an AdvisoryDetail.
     *
     * @param advisoryResponse The AdvisoryResponse to be mapped.
     * @return The corresponding AdvisoryDetail.
     */
    public AdvisoryDetail mapToAdvisoryDetail(AdvisoryResponse advisoryResponse) {
        Optional<AdvisoryDetail> advisoryDetailOptional = advisoryDetailRepository.findByUrl(advisoryResponse.getUrl());

        return advisoryDetailOptional.orElseGet(() -> AdvisoryDetail.builder()
                .cvss3Score(advisoryResponse.getCvss3Score())
                .cvss3Vector(advisoryResponse.getCvss3Vector())
                .title(advisoryResponse.getTitle())
                .url(advisoryResponse.getUrl())
                .aliases(advisoryResponse.getAliases())
                .build());

    }

    /**
     * Maps a Dependency to a PackageResponseDto.
     *
     * @param dependency The Dependency entity to be mapped.
     * @return The corresponding PackageResponseDto.
     */
    public PackageResponseDto mapToPackageResponse(Dependency dependency) {
        return PackageResponseDto.builder()
                .packageKey(mapToPackageKey(dependency))
                .versions(dependency.getVersions()
                        .stream()
                        .map(this::mapToVersionDto)
                        .toList())
                .build();
    }

    /**
     * Maps a Dependency to a PackageKeyDto.
     *
     * @param dependency The Dependency entity to be mapped.
     * @return The corresponding PackageKeyDto.
     */
    public PackageKeyDto mapToPackageKey(Dependency dependency) {
        return PackageKeyDto.builder()
                .name(dependency.getName())
                .system(dependency.getSystem().getSystem())
                .build();
    }

    /**
     * Maps a Version to a VersionDto.
     *
     * @param version The Version entity to be mapped.
     * @return The corresponding VersionDto.
     */
    public VersionDto mapToVersionDto(Version version) {
        return VersionDto.builder()
                .versionKey(mapToVersionKeyDto(version))
                .isDefault(String.valueOf(version.getVersionDetail().getIsDefault()))
                .publishedAt(version.getVersionDetail().getPublishedAt())
                .build();
    }

    /**
     * Maps a Version to a VersionKeyDto.
     *
     * @param version The Version entity to be mapped.
     * @return The corresponding VersionKeyDto.
     */
    public VersionKeyDto mapToVersionKeyDto(Version version) {
        return VersionKeyDto.builder()
                .name(version.getDependency().getName())
                .system(version.getDependency().getSystem().getSystem())
                .version(version.getVersion())
                .build();
    }

    /**
     * Maps a PackageResponseDto to a Dependency entity.
     *
     * @param responseDto The PackageResponseDto to be mapped.
     * @return The corresponding Dependency entity.
     */
    public Dependency mapToDependency(PackageResponseDto responseDto) {
        Dependency dependency = Dependency.builder()
                .name(responseDto.getPackageKey().getName())
                .system(mapToSystem(responseDto.getPackageKey().getSystem()))
                .build();

        dependency.setVersions(responseDto.getVersions()
                .stream()
                .map(version -> mapToDependencyVersion(version, dependency))
                .toList());
        dependencyRepository.save(dependency);
        return dependency;
    }

    /**
     * Maps a VersionDto and Dependency to a Version entity.
     *
     * @param versionDto  The VersionDto to be mapped.
     * @param dependency  The corresponding Dependency entity.
     * @return The corresponding Version entity.
     */
    public Version mapToDependencyVersion(VersionDto versionDto, Dependency dependency) {
        Optional<Version> versionOptional = versionRepository
                .findByDependency_NameAndDependency_System_SystemAndVersion
                        (dependency.getName(), dependency.getSystem().getSystem(), versionDto.getVersionKey().getVersion());

        if (versionOptional.isPresent()) {
            Version version = versionOptional.get();
            version.setVersionDetail(mapToVersionDetail(versionDto, version));
            return version;
        }

        Version version = Version.builder()
                .dependency(dependency)
                .version(versionDto.getVersionKey().getVersion())
                .build();
        version.setVersionDetail(mapToVersionDetail(versionDto, version));
        return version;
    }

    /**
     * Maps a VersionDto and Version to a VersionDetail entity.
     *
     * @param versionDto The VersionDto to be mapped.
     * @param version    The corresponding Version entity.
     * @return The corresponding VersionDetail entity.
     */
    public VersionDetail mapToVersionDetail(VersionDto versionDto, Version version) {
        Optional<VersionDetail> versionDetailOptional = versionDetailRepository.findByVersion(version);

        return versionDetailOptional.orElseGet(() -> VersionDetail.builder()
                .version(version)
                .publishedAt(versionDto.getPublishedAt())
                .isDefault(Boolean.valueOf(versionDto.getIsDefault()))
                .build());
    }

    /**
     * Maps a system name to a SystemEntity.
     *
     * @param system The system name to be mapped.
     * @return The corresponding SystemEntity.
     */
    public SystemEntity mapToSystem(String system) {
        return systemRepository.findBySystem(system)
                .orElseGet(() -> SystemEntity.builder().system(system).build());
    }

    /**
     * Maps name, system, and version to a VulnCheckResponse with isDataAvailable set to false.
     *
     * @param name    The name of the dependency.
     * @param system  The system of the dependency.
     * @param version The version of the dependency.
     * @return The corresponding VulnCheckResponse.
     */
    public VulnCheckResponse mapToVulnCheckResponse(String name, String system, String version) {
        return VulnCheckResponse.builder()
                .name(name)
                .system(system)
                .version(version)
                .isDataAvailable(false)
                .build();
    }

    /**
     * Maps a Version entity to a VulnCheckResponse with isDataAvailable set to true.
     *
     * @param dependencyVersion The Version entity to be mapped.
     * @return The corresponding VulnCheckResponse.
     */
    public VulnCheckResponse mapToVulnCheckResponse(Version dependencyVersion) {
        return VulnCheckResponse.builder()
                .name(dependencyVersion.getDependency().getName())
                .system(dependencyVersion.getDependency().getSystem().getSystem())
                .version(dependencyVersion.getVersion())
                .isDataAvailable(true)
                .build();
    }

    /**
     * Maps a VersionsResponseDto and Version to a VersionDetail entity.
     *
     * @param versionsResponseDto The VersionsResponseDto to be mapped.
     * @param version             The corresponding Version entity.
     * @return The corresponding VersionDetail entity.
     */
    public VersionDetail mapToVersionDetail(VersionsResponseDto versionsResponseDto, Version version) {
        return VersionDetail.builder()
                .isDefault(versionsResponseDto.getIsDefault())
                .publishedAt(versionsResponseDto.getPublishedAt())
                .version(version)
                .build();
    }

    /**
     * Maps a license string to a License entity.
     *
     * @param licenseDto The license string to be mapped.
     * @return The corresponding License entity.
     */
    public License mapToLicenseEntity(String licenseDto) {
        Optional<License> licenseOptional = licenseRepository.findByLicense(licenseDto);

        return licenseOptional.orElseGet(() -> License.builder()
                .license(licenseDto)
                .build());
    }

    /**
     * Maps an AdvisoryKeyDto to an AdvisoryKey entity.
     *
     * @param advisoryKeyDto The AdvisoryKeyDto to be mapped.
     * @return The corresponding AdvisoryKey entity.
     */
    public AdvisoryKey mapToAdvisoryKeyEntity(AdvisoryKeyDto advisoryKeyDto) {
        Optional<AdvisoryKey> advisoryKeyOptional = advisoryKeyRepository.findByAdvisoryId(advisoryKeyDto.getId());

        return advisoryKeyOptional.orElseGet(() -> AdvisoryKey.builder()
                .advisoryId(advisoryKeyDto.getId())
                .build());
    }

    /**
     * Maps a LinkDto to a Link entity.
     *
     * @param linkDto The LinkDto to be mapped.
     * @return The corresponding Link entity.
     */
    public Link mapToLinkEntity(LinkDto linkDto) {
        Optional<Link> linkOptional = linkRepository.findByLabelAndUrl(linkDto.getLabel(), linkDto.getUrl());
        return linkOptional.orElseGet(() -> Link.builder().label(linkDto.getLabel()).url(linkDto.getUrl()).build());
    }
}