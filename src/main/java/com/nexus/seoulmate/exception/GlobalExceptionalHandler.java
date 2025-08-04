package com.nexus.seoulmate.exception;

import com.nexus.seoulmate.exception.status.ErrorStatus;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionalHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<Response<?>> handleDuplicateException(CustomException ex) {
        ErrorStatus errorCode = ex.getErrorCode();

        ex.printStackTrace();
        return new ResponseEntity<>(Response.fail(errorCode), errorCode.getStatus());
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response<?>> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity
                .badRequest()
                .body(Response.fail(ErrorStatus.INVALID_PARAMETER));
    }
}
