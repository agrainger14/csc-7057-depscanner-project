package com.depscanner.projectservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoUserProjectsException extends RuntimeException {
    private String message;
    public NoUserProjectsException(String message) {
        super(message);
    }
}
