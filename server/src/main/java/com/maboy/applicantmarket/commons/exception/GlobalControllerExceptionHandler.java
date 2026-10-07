package com.maboy.applicantmarket.commons.exception;

import com.maboy.applicantmarket.commons.model.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(IncorrectRequestDataException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectRequestDataException(IncorrectRequestDataException exception, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(AlreadyExistsException exception, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException exception, WebRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(AccessForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleAccessForbiddenException(AccessForbiddenException exception, WebRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException exception, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ServerUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServerUnavailableException(ServerUnavailableException exception, WebRequest request) {
        return buildErrorResponse(HttpStatus.GATEWAY_TIMEOUT, exception.getMessage());
    }
    @ExceptionHandler(ApplicantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleApplicantNotFoundException(ApplicantNotFoundException exception, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }
    @ExceptionHandler(FspIdAlreadyLinkedException.class)
    public ResponseEntity<ErrorResponse> handleFspIdAlreadyLinkedException(FspIdAlreadyLinkedException exception, WebRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
    }
    @ExceptionHandler(GradeChangeCooldownException.class)
    public ResponseEntity<ErrorResponse> handleGradeChangeCooldownException(GradeChangeCooldownException exception, WebRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(status.value())
                .errorMessage(message)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}