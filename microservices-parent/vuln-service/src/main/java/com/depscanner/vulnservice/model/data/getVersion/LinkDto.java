package com.depscanner.vulnservice.model.data.getVersion;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class LinkDto {
    private String label;
    private String url;
}