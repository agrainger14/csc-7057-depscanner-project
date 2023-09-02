package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link Link} entities
 * in the database, with methods for querying and interacting with link entities.
 */
@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByLabelAndUrl(String label, String url);
}
