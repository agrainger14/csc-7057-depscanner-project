package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.Version;
import com.depscanner.vulnservice.model.entity.VersionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VersionDetailRepository extends JpaRepository<VersionDetail, Long> {
    Optional<VersionDetail> findByVersion(Version version);

}
