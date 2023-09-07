package com.depscanner.vulnservice.service;

import com.depscanner.vulnservice.depsdev.ApiHelper;
import com.depscanner.vulnservice.exception.NoDependencyInformationException;
import com.depscanner.vulnservice.mapper.Mapper;
import com.depscanner.vulnservice.model.data.getPackage.PackageResponseDto;
import com.depscanner.vulnservice.model.entity.Dependency;
import com.depscanner.vulnservice.model.entity.Version;
import com.depscanner.vulnservice.repository.DependencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing package data, including reading and fetching package information.
 * This service provides methods to read package information by name and system,
 * fetch package information from the deps.dev API and create or update package data in the database.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GetPackageService {
    private final Mapper mapper;
    private final DependencyRepository dependencyRepository;

    /**
     * Reads package information by name and system.
     *
     * @param name   The name of the package.
     * @param system The system of the package.
     * @return A {@link PackageResponseDto} containing package information.
     * @throws NoDependencyInformationException if no package data is available.
     */
    public PackageResponseDto readPackage(String name, String system) {
        return fetchPackageData(name, system);
    }

    /**
     * Fetches package information from the deps.dev API by name and system.
     *
     * @param name   The name of the package.
     * @param system The system of the package.
     * @return A {@link PackageResponseDto} containing the fetched package information.
     * @throws NoDependencyInformationException if no package data is available.
     */
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

    /**
     * Checks package data and updates or creates a dependency entity accordingly.
     *
     * @param responseDto The package response DTO containing package data.
     * @return A {@link PackageResponseDto} containing package information.
     */
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

    /**
     * Updates package versions for an existing dependency entity.
     *
     * @param dependency   The dependency entity to update.
     * @param responseDto  The package response DTO containing updated package data.
     */
    private void updatePackageVersions(Dependency dependency, PackageResponseDto responseDto) {
        if (dependency.getVersions().size() != responseDto.getVersions().size()) {
            List<Version> updatedVersions = responseDto.getVersions()
                    .stream()
                    .map(version -> mapper.mapToDependencyVersion(version, dependency))
                    .collect(Collectors.toList());
            dependency.setVersions(updatedVersions);
            dependencyRepository.save(dependency);
        }
    }
}
