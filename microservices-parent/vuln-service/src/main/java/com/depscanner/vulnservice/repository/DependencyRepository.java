package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.Dependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link Dependency} entities
 * in the database, with methods for querying and interacting with open-source dependencies.
 */
@Repository
public interface DependencyRepository extends JpaRepository<Dependency, Long> {
    Optional<Dependency> findByNameAndSystem_System(String name, String system);
}
