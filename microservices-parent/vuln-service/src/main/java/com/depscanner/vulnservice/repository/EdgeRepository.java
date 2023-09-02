package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.Edge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and managing {@link Edge} entities
 * in the database, with methods for querying and interacting with edge entities.
 */
@Repository
public interface EdgeRepository extends JpaRepository<Edge, Long> {
}
