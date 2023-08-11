package com.depscanner.projectservice.model.data.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanUpdateRequest {
    @NotNull(message = "isDailyScanned must not be null")
    private boolean isDailyScanned;

    @NotNull(message = "isWeeklyScanned must not be null")
    private boolean isWeeklyScanned;
}
