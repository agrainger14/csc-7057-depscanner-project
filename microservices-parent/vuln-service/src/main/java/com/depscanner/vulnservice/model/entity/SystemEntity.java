package com.depscanner.vulnservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a system entity, including its unique identifier and system name.
 *
 * This entity class is used to model systems or software components, where each system
 * may have a unique identifier and a name that identifies it.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "system")
public class SystemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String system;
}
