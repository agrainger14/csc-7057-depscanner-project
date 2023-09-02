package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.RelatedDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and managing {@link RelatedDependency} entities
 * in the database, with methods for querying and interacting with related dependency entities.
 */
@Repository
public interface RelatedDependencyRepository extends JpaRepository<RelatedDependency, Long> {
}
