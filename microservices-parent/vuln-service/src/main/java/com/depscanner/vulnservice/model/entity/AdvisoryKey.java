package com.depscanner.vulnservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a key associated with an advisory, including its identifier and details.
 *
 * This entity class stores a unique identifier for an advisory key, which is linked
 * to an advisory detail containing additional information about the advisory.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "advisory_key")
public class AdvisoryKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String advisoryId;

    @OneToOne(cascade = CascadeType.ALL)
    private AdvisoryDetail advisoryDetail;
}