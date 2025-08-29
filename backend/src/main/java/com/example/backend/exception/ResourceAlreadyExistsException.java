package com.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends BusinessException {
    public ResourceAlreadyExistsException(String errorCode, Object... args) {
        super(errorCode, args);
    }
}
