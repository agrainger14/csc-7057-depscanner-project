package com.depscanner.projectservice.model.data.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScanUpdateResponse {
    private String id;
    private boolean isDailyScanned;
    private boolean isWeeklyScanned;
}
