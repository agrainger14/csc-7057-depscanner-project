package com.depscanner.projectservice.model.data.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteResponse {
    boolean success;
    String message;
}
