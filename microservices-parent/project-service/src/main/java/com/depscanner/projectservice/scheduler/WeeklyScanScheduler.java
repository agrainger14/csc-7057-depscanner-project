package com.depscanner.projectservice.scheduler;

import com.depscanner.projectservice.event.VulnScanEvent;
import com.depscanner.projectservice.model.data.dto.DependencyDto;
import com.depscanner.projectservice.model.data.response.ProjectResponse;
import com.depscanner.projectservice.model.entity.ProjectEntity;
import com.depscanner.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component class responsible for scheduling weekly vulnerability scans for projects.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WeeklyScanScheduler {
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, VulnScanEvent> kafkaTemplate;

    /**
     * Scheduled method to initiate weekly vulnerability scans for set projects.
     * Runs weekly at 2:00 AM (Europe/London timezone).
     */
    @Scheduled(cron = "0 0 2 * * *", zone = "Europe/London")
    public void scanWeeklyProjects() {
        log.info("WEEKLY SCHEDULER BEGUN");
        List<ProjectEntity> dailyProjects = projectRepository.findByIsWeeklyScannedTrue();

        for (ProjectEntity project : dailyProjects) {
            log.info("WEEKLY SCAN: project with Id -> " +  project.getId());
            final String userEmail = project.getEmail();

            if (project.getDependencies() != null) {
                List<DependencyDto> dependencyList = project.getDependencies()
                        .stream()
                        .map(dependency -> modelMapper.map(dependency, DependencyDto.class))
                        .toList();

                VulnScanEvent vulnScanEvent = new VulnScanEvent(userEmail, modelMapper.map(project, ProjectResponse.class), dependencyList);
                kafkaTemplate.send("project-vuln-scan-topic", vulnScanEvent);
            }
        }

        log.info("WEEKLY SCHEDULER CONCLUDED");
    }
}
