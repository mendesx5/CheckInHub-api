package com.mendes.check_in_hub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiError> buildResponse(
            HttpStatus status,
            String message
    ) {
        ApiError apiError = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(status)
                .body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid data");

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                message
        );
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ApiError> handleEventNotFound(EventNotFoundException e) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException e) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
    }

    @ExceptionHandler(EnrollmentNotFoundException.class)
    public ResponseEntity<ApiError> handleEnrollmentNotFound(EnrollmentNotFoundException e) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<ApiError> handleUnauthorizedOperation(UnauthorizedOperationException e) {
        return buildResponse(
                HttpStatus.FORBIDDEN,
                e.getMessage()
        );
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusinessRule(BusinessRuleException e) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception e) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
    }

}
