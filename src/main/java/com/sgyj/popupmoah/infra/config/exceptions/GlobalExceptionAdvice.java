package com.sgyj.popupmoah.infra.config.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = AlreadyExistInfoException.class)
    public ResponseEntity handleAlreadyExistInfoException(AlreadyExistInfoException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(value = UserException.class)
    public ResponseEntity handleUserException(UserException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
