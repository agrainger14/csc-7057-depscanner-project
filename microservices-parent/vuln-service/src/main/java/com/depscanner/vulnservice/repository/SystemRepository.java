package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.SystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemRepository extends JpaRepository<SystemEntity, Long> {
    Optional<SystemEntity> findBySystem(String system);
}
