package com.depscanner.vulnservice.controller;

import com.depscanner.vulnservice.model.data.Request.DependencyRequest;
import com.depscanner.vulnservice.model.data.Response.RelatedDependencyResponse;
import com.depscanner.vulnservice.model.data.Response.VulnCheckResponse;
import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryKeyDto;
import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryResponse;
import com.depscanner.vulnservice.model.data.getPackage.PackageResponseDto;
import com.depscanner.vulnservice.model.data.getVersion.VersionsResponseDto;
import com.depscanner.vulnservice.service.GetAdvisoryService;
import com.depscanner.vulnservice.service.GetDependenciesService;
import com.depscanner.vulnservice.service.GetPackageService;
import com.depscanner.vulnservice.service.GetVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RestController class that defines various endpoints for querying open-source dependency vulnerability related information
 * and performing vulnerability checks on dependencies.
 */
@RestController
@RequiredArgsConstructor
public class VulnController {

    private final GetAdvisoryService getAdvisoryService;
    private final GetDependenciesService getDependenciesService;
    private final GetVersionService getVersionService;
    private final GetPackageService getPackageService;

    /**
     * Retrieves the detailed information about a specific advisory using its advisory key.
     *
     * @param advisoryKey The advisory key for which to retrieve details.
     * @return ResponseEntity containing the AdvisoryResponse with advisory details.
     */
    @GetMapping("/advisory/{advisoryKey}")
    public ResponseEntity<AdvisoryResponse> getAdvisoryDetail(@PathVariable AdvisoryKeyDto advisoryKey) {
        return ResponseEntity.ok(getAdvisoryService.readByAdvisoryKey(advisoryKey));
    }

    /**
     * Retrieves all available versions of a dependency package for a given system.
     *
     * @param name   The name of the dependency package.
     * @param system The target system for which to retrieve versions.
     * @return ResponseEntity containing the PackageResponseDto with available versions.
     */
    @GetMapping("/versions")
    public ResponseEntity<PackageResponseDto> getAllDependencyVersions(@RequestParam String name, @RequestParam String system) {
        return ResponseEntity.ok(getPackageService.readPackage(name, system));
    }

    /**
     * Retrieves the dependencies of a specific dependency package and version for a given system.
     *
     * @param name    The name of the dependency package.
     * @param version The version of the dependency package.
     * @param system  The target system for which to retrieve dependencies.
     * @return ResponseEntity containing the RelatedDependencyResponse with dependency details.
     */
    @GetMapping("/dependencies")
    public ResponseEntity<RelatedDependencyResponse> getAllDependencyDependencies(@RequestParam String name, @RequestParam String version,
                                                                                  @RequestParam String system) {
      return ResponseEntity.ok(getDependenciesService.readDependencyDependencies(system, name, version));
    }

    /**
     * Retrieves detailed version information about a specific dependency package version for a given system.
     *
     * @param name    The name of the dependency package.
     * @param version The version of the dependency package.
     * @param system  The target system for which to retrieve version details.
     * @return ResponseEntity containing the VersionsResponseDto with version details.
     */
    @GetMapping("/dependency")
    public ResponseEntity<VersionsResponseDto> getDependencyVersionDetail(@RequestParam String name, @RequestParam String version,
                                                                          @RequestParam String system) {
        VersionsResponseDto response = getVersionService.readDependencyVersionDetail(system, name, version);

        if (response == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Performs vulnerability checks on a list of dependency requests.
     *
     * @param dependencyRequestList List of dependency requests to check for vulnerabilities.
     * @return ResponseEntity containing a list of VulnCheckResponse indicating vulnerability status.
     */
    @PostMapping("/check")
    public ResponseEntity<List<VulnCheckResponse>> checkVulnerableDependencies(@RequestBody List<DependencyRequest> dependencyRequestList) {
        return ResponseEntity.ok(getVersionService.checkIfDependenciesVulnerable(dependencyRequestList));
    }
}
