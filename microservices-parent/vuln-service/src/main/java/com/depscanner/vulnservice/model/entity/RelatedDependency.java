package com.depscanner.vulnservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Represents a related open-source dependency entity, including its unique identifier,
 * associated version, bundled status, relation and a list of errors.
 *
 * This entity class is used to model related dependencies, where each related dependency
 * may be associated with a specific version and includes information about bundling,
 * relationship and any errors if applicable.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "related_dependency")
public class RelatedDependency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_id")
    private Version version;

    private Boolean bundled;
    private String relation;
    private List<String> errors;
}
