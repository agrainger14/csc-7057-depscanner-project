package com.depscanner.vulnservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * Provides controller with the appropriate error details from the specified exception class.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoPackageInformationException.class)
    public ResponseEntity<ErrorDetails> handleNoPackageInformationException(NoPackageInformationException exception, WebRequest webRequest) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(exception.getMessage())
                .path(webRequest.getDescription(false))
                .errorCode("NO_PACKAGE_INFORMATION_AVAILABLE")
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoAdvisoryKeyInformationException.class)
    public ResponseEntity<ErrorDetails> handleNoAdvisoryKeyInformationException(NoAdvisoryKeyInformationException exception, WebRequest webRequest) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(exception.getMessage())
                .path(webRequest.getDescription(false))
                .errorCode("NO_ADVISORY_INFORMATION_AVAILABLE")
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorDetails> handleInvalidUrlException(InvalidUrlException exception, WebRequest webRequest) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(exception.getMessage())
                .path(webRequest.getDescription(false))
                .errorCode("INVALID_URL")
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoDependencyInformationException.class)
    public ResponseEntity<ErrorDetails> handleNoDependencyInformationException(NoDependencyInformationException exception, WebRequest webRequest) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(exception.getMessage())
                .path(webRequest.getDescription(false))
                .errorCode("NO_DEPENDENCY_VERSION_INFORMATION_AVAILABLE")
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
