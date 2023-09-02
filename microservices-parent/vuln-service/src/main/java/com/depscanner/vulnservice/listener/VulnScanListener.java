package com.depscanner.vulnservice.listener;

import com.depscanner.vulnservice.depsdev.ApiHelper;
import com.depscanner.vulnservice.event.AdvisoryFoundEvent;
import com.depscanner.vulnservice.event.VulnDependency;
import com.depscanner.vulnservice.event.VulnScanEvent;
import com.depscanner.vulnservice.exception.InvalidUrlException;
import com.depscanner.vulnservice.model.data.getDependencies.DependencyGraphResponseDto;
import com.depscanner.vulnservice.model.data.getVersion.DependencyDto;
import com.depscanner.vulnservice.model.data.getVersion.VersionsResponseDto;
import com.depscanner.vulnservice.service.GetDependenciesService;
import com.depscanner.vulnservice.service.GetVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Kafka listener component that scans for vulnerabilities and processes dependency information.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VulnScanListener {
    private final GetDependenciesService getDependenciesService;
    private final GetVersionService getVersionService;

    private final KafkaTemplate<String, AdvisoryFoundEvent> kafkaTemplate;

    /**
     * Kafka listener method that scans for vulnerabilities based on the received VulnScanEvent.
     *
     * @param vulnScanEvent The event containing vulnerability scan information.
     */
    @KafkaListener(topics = "project-vuln-scan-topic")
    public void scanForVulnerabilities(VulnScanEvent vulnScanEvent) {
        log.info("Scanning new project with dependency count -> " + vulnScanEvent.getDependencies().size());

        Set<VulnDependency> vulnDependencySet = processDependency(vulnScanEvent.getDependencies());

        log.info("PROCESSING COMPLETE...");

        if (!vulnDependencySet.isEmpty()) {
            log.info("Vulnerable dependencies detected in project... passing to notification service");
            AdvisoryFoundEvent advisoryFoundEvent = new AdvisoryFoundEvent(vulnScanEvent.getUserEmail(),
                    vulnScanEvent.getProjectResponse(), vulnDependencySet);
            kafkaTemplate.send("advisory-found-topic", advisoryFoundEvent);
        }
    }

    /**
     * Processes the list of dependencies for vulnerabilities.
     *
     * @param dependencies The list of DependencyDto objects representing project dependencies.
     * @return A set of VulnDependency objects containing vulnerability information.
     */
    @Transactional
    public Set<VulnDependency> processDependency(List<DependencyDto> dependencies) {
        Set<VulnDependency> vulnDependencyList = new HashSet<>();
        Set<DependencyDto> visitedDependencies = new HashSet<>();
        Deque<DependencyDto> dependencyDeque = new ArrayDeque<>(dependencies);

        while (!dependencyDeque.isEmpty()) {
            DependencyDto currentDependency = dependencyDeque.pop();

            String name = currentDependency.getName();
            String version = currentDependency.getVersion();
            String system = currentDependency.getSystem();

            if (visitedDependencies.contains(currentDependency)) {
                continue; // Don't need to revisit this again. We already got the data from deps.dev API.
            }

            visitedDependencies.add(currentDependency);
            log.info("Scanning : " + name + " | " + system + " | " + version);
            VersionsResponseDto dependencyVersionData = getVersionData(currentDependency);

            if (dependencyVersionData != null && !dependencyVersionData.getAdvisoryKeys().isEmpty()) {
                addVulnDependency(currentDependency, vulnDependencyList, dependencyVersionData);
                // Any vulnerabilities in this method all affect the "parent" dependency.
            }

            if (dependencies.contains(currentDependency)) {
                List<DependencyDto> dependencyDependenciesList = getDependencyDependencies
                        (currentDependency.getSystem(), currentDependency.getVersion(), currentDependency.getName());
                dependencyDeque.addAll(dependencyDependenciesList);
            }
        }
        return vulnDependencyList;
    }


    /**
     * Adds vulnerability information to an existing or new VulnDependency object.
     *
     * @param dependency           The DependencyDto for which vulnerabilities are being added.
     * @param vulnDependencyList   The set of VulnDependency objects to be updated.
     * @param dependencyVersionData The VersionsResponseDto containing version and advisory information.
     */
    private void addVulnDependency(DependencyDto dependency, Set<VulnDependency> vulnDependencyList, VersionsResponseDto dependencyVersionData) {
        Optional<VulnDependency> vulnDependencyOptional = vulnDependencyList.stream()
                .filter(vulnDependency -> vulnDependency.getDependency().equals(dependency))
                .findFirst();

        if (vulnDependencyOptional.isPresent()) {
            VulnDependency existingDependency = vulnDependencyOptional.get();
            existingDependency.getAdvisoryKeys().addAll(dependencyVersionData.getAdvisoryKeys());
        } else {
            VulnDependency vulnDependency = VulnDependency.builder()
                    .dependency(dependency)
                    .advisoryKeys(new HashSet<>(dependencyVersionData.getAdvisoryKeys()))
                    .build();
            vulnDependencyList.add(vulnDependency);
        }
    }

    /**
     * Retrieves the list of dependencies for a specific dependency version.
     *
     * @param system  The system of the dependency.
     * @param version The version of the dependency.
     * @param name    The name of the dependency.
     * @return A list of DependencyDto objects representing the dependencies of the given dependency version.
     */
    private List<DependencyDto> getDependencyDependencies(String system, String version, String name) {
        String getDependenciesUrl = ApiHelper.buildApiUrl(ApiHelper.GET_DEPENDENCY_URL,
                system, ApiHelper.percentEncodeParam(name), version);

        isUrlValid(getDependenciesUrl);
        DependencyGraphResponseDto responseDto = ApiHelper.makeApiRequest(getDependenciesUrl, DependencyGraphResponseDto.class);

        return Optional.ofNullable(responseDto)
                .map(dependencyGraph -> {
                    getDependenciesService.addDependencyDependencies(responseDto);
                    return dependencyGraph.getNodes().stream()
                            .map(node -> DependencyDto.builder()
                                    .name(node.getVersionKey().getName())
                                    .system(node.getVersionKey().getSystem())
                                    .version(node.getVersionKey().getVersion())
                                    .build())
                            .toList();
                })
                .orElse(Collections.emptyList());
    }

    /**
     * Retrieves version information for a specific dependency.
     *
     * @param dependency The DependencyDto object representing the dependency.
     * @return The VersionsResponseDto containing version information and advisories.
     */
    public VersionsResponseDto getVersionData(DependencyDto dependency) {
        String getVersionUrl = ApiHelper.buildApiUrl(ApiHelper.GET_VERSION_URL,
                dependency.getSystem(), ApiHelper.percentEncodeParam(dependency.getName()),
                dependency.getVersion());

        isUrlValid(getVersionUrl);

        VersionsResponseDto versionsResponseDto = ApiHelper.makeApiRequest(getVersionUrl, VersionsResponseDto.class);

        if (versionsResponseDto != null) {
            getVersionService.addDependencyData(versionsResponseDto);
        }
        return versionsResponseDto;
    }

    /**
     * Checks if a given API URL is valid.
     *
     * @param apiUrl The API URL to be checked for validity.
     * @throws InvalidUrlException If the URL contains placeholders or variables.
     */
    private void isUrlValid(String apiUrl) {
        if (apiUrl.contains("$") || apiUrl.contains("{")) {
            throw new InvalidUrlException("Invalid URL passed! Check request params.");
        }
    }
}