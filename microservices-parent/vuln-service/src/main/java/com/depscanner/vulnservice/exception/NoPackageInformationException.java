package com.depscanner.vulnservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoPackageInformationException extends RuntimeException {
    private String message;

    public NoPackageInformationException(String message) {
        super(message);
    }
}
