package com.depscanner.vulnservice.model.data.getPackage;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageResponseDto {
    private PackageKeyDto packageKey;
    private List<VersionDto> versions;
}
