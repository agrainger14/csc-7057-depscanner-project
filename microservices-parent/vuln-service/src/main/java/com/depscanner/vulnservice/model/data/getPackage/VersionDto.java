package com.depscanner.vulnservice.model.data.getPackage;

import lombok.*;

import java.time.LocalDateTime;

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
