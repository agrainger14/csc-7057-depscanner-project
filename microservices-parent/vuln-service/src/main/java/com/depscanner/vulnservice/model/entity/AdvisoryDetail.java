package com.depscanner.vulnservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * Represents details related to an advisory.
 * This entity class stores information about an advisory, including its URL,
 * title, aliases, CVSS (Common Vulnerability Scoring System) version 3 score,
 * and CVSS version 3 vector string.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "advisory_detail")
public class AdvisoryDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String title;

    @ElementCollection
    private Set<String> aliases;

    private double cvss3Score;
    private String cvss3Vector;
}
