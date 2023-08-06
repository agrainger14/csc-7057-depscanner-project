package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.RelatedDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatedDependencyRepository extends JpaRepository<RelatedDependency, Long> {
}
