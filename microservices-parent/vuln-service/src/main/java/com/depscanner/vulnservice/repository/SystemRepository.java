package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.SystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link SystemEntity} entities
 * in the database, with methods for querying and interacting with system entities.
 */
@Repository
public interface SystemRepository extends JpaRepository<SystemEntity, Long> {
    Optional<SystemEntity> findBySystem(String system);
}
