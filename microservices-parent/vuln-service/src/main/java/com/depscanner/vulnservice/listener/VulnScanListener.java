package com.depscanner.vulnservice.listener;

import com.depscanner.vulnservice.depsdev.ApiHelper;
import com.depscanner.vulnservice.event.AdvisoryFoundEvent;
import com.depscanner.vulnservice.event.VulnDependency;
import com.depscanner.vulnservice.event.VulnScanEvent;
import com.depscanner.vulnservice.exception.InvalidUrlException;
import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryKeyDto;
import com.depscanner.vulnservice.model.data.getAdvisory.AdvisoryResponse;
import com.depscanner.vulnservice.model.data.getVersion.DependencyDto;
import com.depscanner.vulnservice.model.data.getDependencies.DependencyGraphResponseDto;
import com.depscanner.vulnservice.model.data.getVersion.VersionsResponseDto;
import com.depscanner.vulnservice.service.GetAdvisoryService;
import com.depscanner.vulnservice.service.GetDependenciesService;
import com.depscanner.vulnservice.service.GetVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class VulnScanListener {
    private final TaskExecutor taskExecutor;
    private final GetDependenciesService getDependenciesService;
    private final GetVersionService getVersionService;
    private final GetAdvisoryService getAdvisoryService;

    private final KafkaTemplate<String, AdvisoryFoundEvent> kafkaTemplate;

    @KafkaListener(topics = "project-vuln-scan-topic")
    public void scanForVulnerabilities(VulnScanEvent vulnScanEvent) {
        log.info("Scanning new project with dependency count -> " + vulnScanEvent.getDependencies().size());

        Set<VulnDependency> vulnDependencySet = processDependency(vulnScanEvent.getDependencies());

        log.info("PROCESSING COMPLETE...");

        if (vulnDependencySet != null) {
            log.info("Vulnerable dependencies detected in project... passing to notification service");
            AdvisoryFoundEvent advisoryFoundEvent = new AdvisoryFoundEvent(vulnScanEvent.getUserEmail(),
                    vulnScanEvent.getProjectResponse(), vulnDependencySet);
            kafkaTemplate.send("advisory-found-topic", advisoryFoundEvent);
        }
    }

    public Set<VulnDependency> processDependency(List<DependencyDto> dependencies) {
        Set<VulnDependency> vulnDependencyList = new HashSet<>();
        Set<DependencyDto> visitedDependencies = new HashSet<>();
        Deque<DependencyDto> dependencyStack = new ArrayDeque<>(dependencies);

        while (!dependencyStack.isEmpty()) {
            final DependencyDto currentDependency = dependencyStack.pop();

            final String name = currentDependency.getName();
            final String version = currentDependency.getVersion();
            final String system = currentDependency.getSystem();

            log.info("Scanning : " + name + " | " + system + " | " + version);

            if (visitedDependencies.contains(currentDependency)) {
                continue; // Don't need to revisit this again. We already got the data from deps.dev API.
            }

            visitedDependencies.add(currentDependency);
            VersionsResponseDto dependencyVersionData = getVersionData(currentDependency);

            if (dependencyVersionData != null && !dependencyVersionData.getAdvisoryKeys().isEmpty()) {
                addVulnDependency(currentDependency, vulnDependencyList, dependencyVersionData);
                // Any vulnerabilities in this method all affect the "parent" dependency.
            }

            if (dependencies.contains(currentDependency)) {
                List<DependencyDto> dependencyDependenciesList = getDependencyDependencies
                        (currentDependency.getSystem(), currentDependency.getVersion(), currentDependency.getName());
                dependencyStack.addAll(dependencyDependenciesList);
            }
        }
        return vulnDependencyList;
    }


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

    public VersionsResponseDto getVersionData(DependencyDto dependency) {
        String getVersionUrl = ApiHelper.buildApiUrl(ApiHelper.GET_VERSION_URL,
                dependency.getSystem(), ApiHelper.percentEncodeParam(dependency.getName()),
                dependency.getVersion());

        isUrlValid(getVersionUrl);

        VersionsResponseDto versionsResponseDto = ApiHelper.makeApiRequest(getVersionUrl, VersionsResponseDto.class);

        if (versionsResponseDto != null) {
            getVersionService.addDependencyData(versionsResponseDto);

            if (versionsResponseDto.getAdvisoryKeys().size() > 0) {
                getAdvisoryData(versionsResponseDto.getAdvisoryKeys());
            }
        }
        return versionsResponseDto;
    }


    public void getAdvisoryData(List<AdvisoryKeyDto> advisoryKey) {
        for (AdvisoryKeyDto advisory : advisoryKey) {
            String getAdvisoryUrl = ApiHelper.buildApiUrl(ApiHelper.GET_ADVISORY_URL, advisory.getId());

            isUrlValid(getAdvisoryUrl);
            AdvisoryResponse responseDto = ApiHelper.makeApiRequest(getAdvisoryUrl, AdvisoryResponse.class);

            if (responseDto != null) {
                getAdvisoryService.createAdvisoryData(responseDto);
            }
        }
    }

    private void isUrlValid(String apiUrl) {
        if (apiUrl.contains("$") || apiUrl.contains("{")) {
            throw new InvalidUrlException("Invalid URL passed! Check request params.");
        }
    }
}