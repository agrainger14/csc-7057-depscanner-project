package com.depscanner.vulnservice.model.data.getPackage;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO representing a version with published at date/time and if the version is the latest version.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VersionDto {
    private VersionKeyDto versionKey;
    private LocalDateTime publishedAt;
    private String isDefault;
}
