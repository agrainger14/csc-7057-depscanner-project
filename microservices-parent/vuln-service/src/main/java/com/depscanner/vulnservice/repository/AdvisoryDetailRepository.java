package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.AdvisoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdvisoryDetailRepository extends JpaRepository<AdvisoryDetail, Long> {
    Optional<AdvisoryDetail> findByUrl(String url);

}
