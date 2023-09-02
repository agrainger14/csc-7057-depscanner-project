package com.depscanner.vulnservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Represents an open-source dependency, including its name, associated system, and versions.
 *
 * This entity class stores information about a software dependency, such as its unique identifier,
 * name, the associated system it belongs to and a list of versions related to this dependency.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "dependency")
public class Dependency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SystemEntity system;

    @OneToMany(mappedBy = "dependency", cascade = CascadeType.ALL)
    private List<Version> versions;
}

