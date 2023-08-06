package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.RelatedDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NodeRepository extends JpaRepository<RelatedDependency, Long> {
    Optional<RelatedDependency> findByVersion_Dependency_NameAndVersion_Dependency_System_SystemAndVersion_Version(String name, String system, String version);
}
