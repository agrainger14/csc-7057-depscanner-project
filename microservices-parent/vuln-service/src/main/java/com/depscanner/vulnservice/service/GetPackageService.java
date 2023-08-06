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

    public PackageResponseDto readPackage(String system, String name) {
        Optional<Dependency> dependencyOptional = dependencyRepository.findByNameAndSystem_System(name, system);

        if (dependencyOptional.isPresent()) {
            return mapper.mapToPackageResponse(dependencyOptional.get());
        }

        //if the package version doesn't exist, we don't have data on it. Because this comes from the file upload,
        //there may be no data available in DB.
        return fetchPackageData(system, name);
    }

    private PackageResponseDto fetchPackageData(String system, String name) {
        String getPackageUrl = ApiHelper.buildApiUrl(ApiHelper.GET_PACKAGE_URL, system, ApiHelper.percentEncodeParam(name));

        PackageResponseDto responseDto =
                Optional.ofNullable(ApiHelper.makeApiRequest(getPackageUrl, PackageResponseDto.class))
                        .orElseThrow(() -> new NoDependencyInformationException("No dependency data available"));

        createPackageData(responseDto);
        return responseDto;
    }

    public void createPackageData(PackageResponseDto responseDto) {
        Optional<Dependency> dependencyOptional = dependencyRepository.findByNameAndSystem_System
                (responseDto.getPackageKey().getName(), responseDto.getPackageKey().getSystem());

        if (dependencyOptional.isPresent()) {
            //check if updated information available
            Dependency dependency = dependencyOptional.get();
            updatePackageVersions(dependency, responseDto);
        } else {
            //save new package information
            mapper.mapToDependency(responseDto);
        }
    }

    private void updatePackageVersions(Dependency dependency, PackageResponseDto responseDto) {
        if (dependency.getVersions().size() != responseDto.getVersions().size()) {
            Dependency dependencyToUpdate = dependencyRepository.findById(dependency.getId()).orElseThrow();
            Set<Version> updatedVersions = responseDto.getVersions()
                    .stream()
                    .map(version -> mapper.mapDependencyVersion(version, dependencyToUpdate))
                    .collect(Collectors.toSet());
            dependencyToUpdate.setVersions(updatedVersions);
            versionRepository.saveAll(updatedVersions);
        }
    }
}
