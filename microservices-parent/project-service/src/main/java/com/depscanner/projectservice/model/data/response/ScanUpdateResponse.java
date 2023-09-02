package com.depscanner.projectservice.model.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Data class representing a response containing information about a user project scanning schedule.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScanUpdateResponse {
    private String id;
    private boolean isDailyScanned;
    private boolean isWeeklyScanned;
}
