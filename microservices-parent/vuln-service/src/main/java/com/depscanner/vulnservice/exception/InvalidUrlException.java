package com.depscanner.vulnservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidUrlException extends RuntimeException {
    private String message;

    public InvalidUrlException(String message) {
        super(message);
    }
}
