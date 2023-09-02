package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link Version} entities
 * in the database, with methods for querying and interacting with version entities.
 */
@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {
    Optional<Version> findByDependency_NameAndDependency_System_SystemAndVersion(String name, String system, String version);
}
