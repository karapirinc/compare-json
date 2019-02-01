package com.tr.karapirinc.comparejson.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgument() {
        // Nothing to do
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    @ExceptionHandler(IllegalStateException.class)
    public void handleIllegalState() {
        // Nothing to do
    }
}
