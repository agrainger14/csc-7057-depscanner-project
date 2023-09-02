package com.depscanner.vulnservice.model.data.getAdvisory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing an advisory key.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvisoryKeyDto {
    private String id;
}
