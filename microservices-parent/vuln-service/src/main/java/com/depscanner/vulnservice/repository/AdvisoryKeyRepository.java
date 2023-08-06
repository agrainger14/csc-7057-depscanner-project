package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.AdvisoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdvisoryKeyRepository extends JpaRepository<AdvisoryKey, Long> {
    Optional<AdvisoryKey> findByAdvisoryId(String advisoryId);

}
