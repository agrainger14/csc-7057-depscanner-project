package com.depscanner.vulnservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents an edge entity with attributes such as identifiers, source node, destination node,
 * and a requirement description.
 *
 * This entity class is used to model connections between nodes, where an edge connects two nodes
 * and may have an associated requirement description.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "edge")
public class Edge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int fromNode;
    private int toNode;
    private String requirement;
}
