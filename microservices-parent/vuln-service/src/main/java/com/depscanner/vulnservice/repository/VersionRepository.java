package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {
    Optional<Version> findByDependency_NameAndDependency_System_SystemAndVersion(String name, String system, String version);


}
