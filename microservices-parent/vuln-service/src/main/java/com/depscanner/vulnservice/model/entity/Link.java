package com.depscanner.vulnservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a link with attributes such as a unique identifier, label and URL.
 *
 * This entity class is used to model links, where each link may have a label describing its purpose
 * and a URL pointing to its destination.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "link")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;
    private String url;
}