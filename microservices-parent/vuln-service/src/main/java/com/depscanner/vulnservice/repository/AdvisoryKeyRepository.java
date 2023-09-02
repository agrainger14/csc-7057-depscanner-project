package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.AdvisoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link AdvisoryKey} entities
 * in the database, with methods for querying and interacting with advisory keys.
 */
@Repository
public interface AdvisoryKeyRepository extends JpaRepository<AdvisoryKey, Long> {
    Optional<AdvisoryKey> findByAdvisoryId(String advisoryId);
}
