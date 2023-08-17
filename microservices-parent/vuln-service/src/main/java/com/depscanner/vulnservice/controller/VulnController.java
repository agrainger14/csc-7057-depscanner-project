package com.depscanner.vulnservice.controller;

import com.depscanner.vulnservice.model.data.Request.DependencyRequest;
import com.depscanner.vulnservice.model.data.Response.RelatedDependencyResponse;
import com.depscanner.vulnservice.model.data.Response.VulnCheckResponse;
import com.depscanner.vulnservice.model.data.Response.VulnDependencyResponse;
import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryKeyDto;
import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryResponse;
import com.depscanner.vulnservice.model.data.getDependencies.DependencyGraphResponseDto;
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
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class VulnController {

    private final GetAdvisoryService getAdvisoryService;
    private final GetDependenciesService getDependenciesService;
    private final GetVersionService getVersionService;
    private final GetPackageService getPackageService;

    @GetMapping("/advisory/{advisoryKey}")
    public ResponseEntity<AdvisoryResponse> getAdvisoryDetail(@PathVariable AdvisoryKeyDto advisoryKey) {
        return ResponseEntity.ok(getAdvisoryService.readByAdvisoryKey(advisoryKey));
    }

    @GetMapping("/versions")
    public ResponseEntity<PackageResponseDto> getAllDependencyVersions(@RequestParam String name, @RequestParam String system) {
        return ResponseEntity.ok(getPackageService.readPackage(name, system));
    }

    @GetMapping("/dependencies")
    public ResponseEntity<RelatedDependencyResponse> getAllDependencyDependencies(@RequestParam String name, @RequestParam String version,
                                                                                  @RequestParam String system) {
      return ResponseEntity.ok(getDependenciesService.readDependencyDependencies(system, name, version));
    }

    @GetMapping("/dependency")
    public ResponseEntity<VersionsResponseDto> getDependencyVersionDetail(@RequestParam String name, @RequestParam String version,
                                                                          @RequestParam String system) {
        VersionsResponseDto response = getVersionService.readDependencyVersionDetail(system, name, version);

        if (response == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/check")
    public ResponseEntity<List<VulnCheckResponse>> checkVulnerableDependencies(@RequestBody List<DependencyRequest> dependencyRequestList) {
        return ResponseEntity.ok(getVersionService.checkIfDependenciesVulnerable(dependencyRequestList));
    }
}
