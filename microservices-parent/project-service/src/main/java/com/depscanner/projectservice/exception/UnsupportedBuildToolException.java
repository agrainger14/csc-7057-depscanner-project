package com.depscanner.projectservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UnsupportedBuildToolException extends RuntimeException {
    private String message;
    public UnsupportedBuildToolException(String message) {
        super(message);
    }
}