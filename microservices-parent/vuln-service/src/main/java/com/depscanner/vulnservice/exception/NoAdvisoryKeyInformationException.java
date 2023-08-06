package com.depscanner.vulnservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoAdvisoryKeyInformationException extends RuntimeException {
    private String message;

    public NoAdvisoryKeyInformationException(String message) {
        super(message);
    }
}
