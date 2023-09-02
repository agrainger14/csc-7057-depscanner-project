package com.depscanner.vulnservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a software license, including license information.
 *
 * This entity class stores information about a software license, such as the license terms.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "license")
public class License {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String license;
}
