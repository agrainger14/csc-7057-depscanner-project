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
import java.util.stream.Collectors;

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

    public Edge mapToEdgeEntity(EdgeDto edge) {
        return Edge.builder()
                .fromNode(edge.getFromNode())
                .toNode(edge.getToNode())
                .requirement(edge.getRequirement())
                .build();
    }

    public RelatedDependency mapToRelatedDependencyEntity(NodeDto nodeDto) {
        return RelatedDependency.builder()
                .bundled(nodeDto.getBundled())
                .errors(nodeDto.getErrors())
                .relation(nodeDto.getRelation())
                .version(mapToVersionEntity(nodeDto.getVersionKey().getName(), nodeDto.getVersionKey().getSystem(), nodeDto.getVersionKey().getVersion()))
                .build();
    }

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

    public RelatedDependencyResponse mapToRelatedDependencyResponse(Version version) {
        return RelatedDependencyResponse.builder()
                .dependency(version.getRelatedDependencies().stream()
                        .map(this::mapToDependencyResponse).toList())
                .edges(version.getEdges().stream()
                        .map(this::mapToEdgeDto).toList())
                .build();
    }

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

    public LinkDto mapToLinkResponse(Link link) {
        return LinkDto.builder()
                .url(link.getUrl())
                .label(link.getLabel())
                .build();
    }

    public String mapToLicenseResponse(License license) {
        return license.getLicense();
    }

    public EdgeDto mapToEdgeDto(Edge edge) {
        return EdgeDto.builder()
                .toNode(edge.getToNode())
                .fromNode(edge.getFromNode())
                .requirement(edge.getRequirement())
                .build();
    }

    public DependencyDto mapToVersionKey(Dependency dependency, String version) {
        return DependencyDto.builder()
                .name(dependency.getName())
                .system(dependency.getSystem().getSystem())
                .version(version)
                .build();
    }


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

    public AdvisoryKeyDto mapToAdvisoryKey(String advisoryId) {
        return AdvisoryKeyDto.builder()
                .id(advisoryId)
                .build();
    }

    public AdvisoryDetail mapToAdvisoryDetail(AdvisoryResponse advisoryResponse) {
        Optional<AdvisoryDetail> advisoryDetailOptional = advisoryDetailRepository.findByUrl(advisoryResponse.getUrl());

        if (advisoryDetailOptional.isPresent()) {
            return advisoryDetailOptional.get();
        }

        return AdvisoryDetail.builder()
                .cvss3Score(advisoryResponse.getCvss3Score())
                .cvss3Vector(advisoryResponse.getCvss3Vector())
                .title(advisoryResponse.getTitle())
                .url(advisoryResponse.getUrl())
                .aliases(advisoryResponse.getAliases())
                .build();
    }

    public PackageResponseDto mapToPackageResponse(Dependency dependency) {
        return PackageResponseDto.builder()
                .packageKey(mapToPackageKey(dependency))
                .versions(dependency.getVersions()
                        .stream()
                        .map(this::mapToVersionDto)
                        .toList())
                .build();
    }

    public PackageKeyDto mapToPackageKey(Dependency dependency) {
        return PackageKeyDto.builder()
                .name(dependency.getName())
                .system(dependency.getSystem().getSystem())
                .build();
    }

    public VersionDto mapToVersionDto(Version version) {
        return VersionDto.builder()
                .versionKey(mapToVersionKeyDto(version))
                .isDefault(String.valueOf(version.getVersionDetail().getIsDefault()))
                .publishedAt(version.getVersionDetail().getPublishedAt())
                .build();
    }

    public VersionKeyDto mapToVersionKeyDto(Version version) {
        return VersionKeyDto.builder()
                .name(version.getDependency().getName())
                .system(version.getDependency().getSystem().getSystem())
                .version(version.getVersion())
                .build();
    }

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

    public Version mapToDependencyVersion(VersionDto versionDto, Dependency dependency) {
        Optional<Version> versionOptional = versionRepository
                .findByDependency_NameAndDependency_System_SystemAndVersion
                        (dependency.getName(), dependency.getSystem().getSystem(), versionDto.getVersionKey().getVersion());

        if (versionOptional.isPresent()) {
            return versionOptional.get();
        } else {
            Version version = Version.builder()
                    .dependency(dependency)
                    .version(versionDto.getVersionKey().getVersion())
                    .build();
            version.setVersionDetail(mapToVersionDetail(versionDto, version));
            return version;
        }
    }

    public VersionDetail mapToVersionDetail(VersionDto versionDto, Version version) {
        Optional<VersionDetail> versionDetailOptional = versionDetailRepository.findByVersion(version);

        if (versionDetailOptional.isPresent()) {
            return versionDetailOptional.get();
        }

        return VersionDetail.builder()
                .version(version)
                .publishedAt(versionDto.getPublishedAt())
                .isDefault(Boolean.valueOf(versionDto.getIsDefault()))
                .build();
    }

    public SystemEntity mapToSystem(String system) {
        return systemRepository.findBySystem(system)
                .orElseGet(() -> SystemEntity.builder().system(system).build());
    }

    public VulnCheckResponse mapToVulnCheckResponse(String name, String system, String version) {
        return VulnCheckResponse.builder()
                .name(name)
                .system(system)
                .version(version)
                .isDataAvailable(false)
                .build();
    }

    public VulnCheckResponse mapToVulnCheckResponse(Version dependencyVersion) {
        return VulnCheckResponse.builder()
                .name(dependencyVersion.getDependency().getName())
                .system(dependencyVersion.getDependency().getSystem().getSystem())
                .version(dependencyVersion.getVersion())
                .isDataAvailable(true)
                .build();
    }

    public VersionsResponseDto mapToDependencyResponse(Version version) {
        return VersionsResponseDto.builder()
                .versionKey(mapToVersionKey(version))
                .isDefault(version.getVersionDetail().getIsDefault())
                .licenses(version.getLicenses().stream()
                        .map(this::mapToLicenseResponse)
                        .toList())
                .advisoryKeys(version.getAdvisoryKeys().stream()
                        .map(this::mapToAdvisoryKeyDto)
                        .toList())
                .links(version.getLinks().stream()
                        .map(this::mapToLinkDto)
                        .toList())
                .publishedAt(version.getVersionDetail().getPublishedAt())
                .build();
    }

    public LinkDto mapToLinkDto(Link link) {
        return LinkDto.builder()
                .label(link.getLabel())
                .url(link.getUrl())
                .build();
    }

    public AdvisoryKeyDto mapToAdvisoryKeyDto(AdvisoryKey advisoryKey) {
        return AdvisoryKeyDto.builder()
                .id(advisoryKey.getAdvisoryId())
                .build();
    }

    public DependencyDto mapToVersionKey(Version version) {
        return DependencyDto.builder()
                .name(version.getDependency().getName())
                .version(version.getVersion())
                .system(version.getDependency().getSystem().getSystem())
                .build();
    }

    public VersionDetail mapToVersionDetail(VersionsResponseDto versionsResponseDto, Version version) {
        return VersionDetail.builder()
                .isDefault(versionsResponseDto.getIsDefault())
                .publishedAt(versionsResponseDto.getPublishedAt())
                .version(version)
                .build();
    }

    public License mapToLicenseEntity(String licenseDto) {
        Optional<License> licenseOptional = licenseRepository.findByLicense(licenseDto);

        return licenseOptional.orElseGet(() -> License.builder()
                .license(licenseDto)
                .build());
    }

    public AdvisoryKey mapToAdvisoryKeyEntity(AdvisoryKeyDto advisoryKeyDto) {
        Optional<AdvisoryKey> advisoryKeyOptional = advisoryKeyRepository.findByAdvisoryId(advisoryKeyDto.getId());

        return advisoryKeyOptional.orElseGet(() -> AdvisoryKey.builder()
                .advisoryId(advisoryKeyDto.getId())
                .build());
    }

    public Link mapToLinkEntity(LinkDto linkDto) {
        Optional<Link> linkOptional = linkRepository.findByLabelAndUrl(linkDto.getLabel(), linkDto.getUrl());
        return linkOptional.orElseGet(() -> Link.builder().label(linkDto.getLabel()).url(linkDto.getUrl()).build());
    }
}