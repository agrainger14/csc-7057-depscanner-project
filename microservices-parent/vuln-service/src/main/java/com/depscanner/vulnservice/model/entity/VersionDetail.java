package com.depscanner.vulnservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents details of an open-source dependency version, including its unique identifier,
 * associated version, publication date, and a flag indicating if it is the default version.
 *
 * This entity class is used to store information about a dependency versions details,
 * such as its unique identifier, the date it was published, and whether it is marked as the default version.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VersionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "versionDetail")
    private Version version;

    private LocalDateTime publishedAt;
    private Boolean isDefault;

}
