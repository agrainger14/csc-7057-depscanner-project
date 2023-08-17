package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.Dependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DependencyRepository extends JpaRepository<Dependency, Long> {
    Optional<Dependency> findByNameAndSystem_System(String name, String system);
}
