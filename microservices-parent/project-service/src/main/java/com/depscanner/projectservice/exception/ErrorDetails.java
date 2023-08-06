package com.depscanner.projectservice.exception;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Error Details to be display response information on bad response
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDetails {
    private LocalDateTime timestamp;
    private String message;
    private String path;
    private String errorCode;
}
