package com.depscanner.projectservice.repository;

import com.depscanner.projectservice.model.entity.ProjectEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB Repository Class
 */
public interface ProjectRepository extends MongoRepository<ProjectEntity, String> {

    /**
     * Method to find all User Projects by the User Email.
     * @param userEmail the users email
     * @return ProjectEntity Optional
     */
    List<ProjectEntity> findAllByEmail(String userEmail);
}
