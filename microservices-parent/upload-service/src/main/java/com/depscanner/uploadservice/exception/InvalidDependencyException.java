package com.depscanner.uploadservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidDependencyException extends RuntimeException {
    public InvalidDependencyException(String message) {
        super(message);
    }
}