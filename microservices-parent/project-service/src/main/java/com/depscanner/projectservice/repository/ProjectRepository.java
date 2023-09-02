package com.depscanner.projectservice.repository;

import com.depscanner.projectservice.model.entity.ProjectEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * MongoDB repository interface for managing project entities.
 */
public interface ProjectRepository extends MongoRepository<ProjectEntity, String> {

    /**
     * Retrieves a list of project entities associated with a specific user email.
     *
     * @param userEmail The email of the user.
     * @return A list of project entities.
     */
    List<ProjectEntity> findAllByEmail(String userEmail);

    /**
     * Retrieves a list of project entities with the "isDailyScanned" flag set to true.
     *
     * @return A list of project entities with daily scanning enabled.
     */
    List<ProjectEntity> findByIsDailyScannedTrue();

    /**
     * Retrieves a list of project entities with the "isWeeklyScanned" flag set to true.
     *
     * @return A list of project entities with weekly scanning enabled.
     */
    List<ProjectEntity> findByIsWeeklyScannedTrue();
}
