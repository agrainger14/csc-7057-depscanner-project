package com.depscanner.projectservice.model.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class representing a response to a deletion operation.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteResponse {
    boolean success;
    String message;
}
