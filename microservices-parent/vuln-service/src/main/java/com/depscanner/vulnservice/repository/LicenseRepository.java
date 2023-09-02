package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link License} entities
 * in the database, with methods for querying and interacting with dependency licenses.
 */
@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
    Optional<License> findByLicense(String license);
}
