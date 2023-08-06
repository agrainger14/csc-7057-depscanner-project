package com.depscanner.vulnservice.model.data.getPackage;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VersionKeyDto {
    private String system;
    private String name;
    private String version;
}
