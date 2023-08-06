package com.depscanner.vulnservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoDependencyInformationException extends RuntimeException {
    private String message;

    public NoDependencyInformationException(String message) {
        super(message);
    }
}