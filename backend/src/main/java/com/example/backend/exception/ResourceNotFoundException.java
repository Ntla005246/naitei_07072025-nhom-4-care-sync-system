package com.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String errorCode, Object... args) {
        super(errorCode, args);
    }

    public ResourceNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
