package com.depscanner.uploadservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoDependenciesParsedException extends RuntimeException {
    private String message;

    public NoDependenciesParsedException(String message) {
        super(message);
    }
}
