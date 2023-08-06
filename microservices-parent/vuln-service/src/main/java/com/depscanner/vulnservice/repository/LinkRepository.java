package com.depscanner.vulnservice.repository;

import com.depscanner.vulnservice.model.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByLabelAndUrl(String label, String url);

}
