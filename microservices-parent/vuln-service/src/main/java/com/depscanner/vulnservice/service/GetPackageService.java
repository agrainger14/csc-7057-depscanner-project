package com.depscanner.vulnservice.service;

import com.depscanner.vulnservice.depsdev.ApiHelper;
import com.depscanner.vulnservice.exception.NoDependencyInformationException;
import com.depscanner.vulnservice.mapper.Mapper;
import com.depscanner.vulnservice.model.data.getPackage.PackageResponseDto;
import com.depscanner.vulnservice.model.entity.*;
import com.depscanner.vulnservice.repository.DependencyRepository;
import com.depscanner.vulnservice.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GetPackageService {
    private final Mapper mapper;
    private final DependencyRepository dependencyRepository;
    private final VersionRepository versionRepository;

    public PackageResponseDto readPackage(String name, String system) {
        return fetchPackageData(name, system);
    }

    private PackageResponseDto fetchPackageData(String name, String system) {
        String getPackageUrl = ApiHelper.buildApiUrl(ApiHelper.GET_PACKAGE_URL, system, ApiHelper.percentEncodeParam(name));

        PackageResponseDto responseDto =
                Optional.ofNullable(ApiHelper.makeApiRequest(getPackageUrl, PackageResponseDto.class))
                        .orElseThrow(() -> new NoDependencyInformationException("No dependency version data available"));


        if (responseDto != null) {
            return checkPackageData(responseDto);
        }

        Optional<Dependency> dependencyOptional = dependencyRepository.findByNameAndSystem_System
                (name, system);

        if (dependencyOptional.isPresent() && dependencyOptional.get().getVersions().size() > 0) {
            return mapper.mapToPackageResponse(dependencyOptional.get());
        }

        throw new NoDependencyInformationException("No dependency version data available");
    }

    public PackageResponseDto checkPackageData(PackageResponseDto responseDto) {
        Optional<Dependency> dependencyOptional = dependencyRepository.findByNameAndSystem_System
                (responseDto.getPackageKey().getName(), responseDto.getPackageKey().getSystem());

        Dependency dependency;

        if (dependencyOptional.isPresent()) {
            //check if updated information available
            dependency = dependencyOptional.get();
            updatePackageVersions(dependency, responseDto);
        } else {
            //create dependency
            dependency = mapper.mapToDependency(responseDto);
        }

        return mapper.mapToPackageResponse(dependency);
    }

    private void updatePackageVersions(Dependency dependency, PackageResponseDto responseDto) {
        if (dependency.getVersions().size() != responseDto.getVersions().size()) {
            List<Version> updatedVersions = responseDto.getVersions()
                    .stream()
                    .map(version -> mapper.mapToDependencyVersion(version, dependency))
                    .toList();
            dependency.setVersions(updatedVersions);
            versionRepository.saveAll(updatedVersions);
        }
    }
}
