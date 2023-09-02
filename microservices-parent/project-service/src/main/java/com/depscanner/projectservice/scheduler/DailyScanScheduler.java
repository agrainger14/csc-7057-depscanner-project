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
 * Component class responsible for scheduling daily vulnerability scans for projects.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DailyScanScheduler {
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, VulnScanEvent> kafkaTemplate;

    /**
     * Scheduled method to initiate daily vulnerability scans for eligible projects.
     * Runs daily at 2:00 AM (Europe/London timezone).
     */
    @Scheduled(cron = "0 0 2 * * ?", zone = "Europe/London")
    public void scanDailyProjects() {
        log.info("DAILY SCHEDULER BEGUN");

        List<ProjectEntity> dailyProjects = projectRepository.findByIsDailyScannedTrue();

        for (ProjectEntity project : dailyProjects) {
            log.info("DAILY SCAN: project with Id -> " +  project.getId());

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

        log.info("DAILY SCHEDULER CONCLUDED");
    }
}
