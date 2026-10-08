package com.maboy.applicantmarket.commons.exception;

import com.maboy.applicantmarket.commons.exception.assessment.AssessmentAnswerAlreadyExistsException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentCooldownException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentItemNotFoundException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentNotReadyForCompletionException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentSessionAlreadyActiveException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentSessionExpiredException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentSessionNotFoundException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentSessionStateException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentTemplateNotFoundException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentTemplatePoolExhaustedException;
import com.maboy.applicantmarket.commons.model.response.ErrorResponse;
import java.util.Map;
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

    @ExceptionHandler(AssessmentSessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAssessmentSessionNotFound(AssessmentSessionNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(AssessmentSessionExpiredException.class)
    public ResponseEntity<ErrorResponse> handleAssessmentSessionExpired(AssessmentSessionExpiredException e) {
        // 410 Gone: сессия существовала, но больше недоступна.
        // Кандидат должен начать новую.
        return buildErrorResponse(HttpStatus.GONE, e.getMessage());
    }

    @ExceptionHandler(AssessmentSessionStateException.class)
    public ResponseEntity<ErrorResponse> handleAssessmentSessionState(AssessmentSessionStateException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(AssessmentSessionAlreadyActiveException.class)
    public ResponseEntity<ErrorResponse> handleAssessmentSessionAlreadyActive(AssessmentSessionAlreadyActiveException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(AssessmentItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAssessmentItemNotFound(AssessmentItemNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(AssessmentAnswerAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAssessmentAnswerAlreadyExists(AssessmentAnswerAlreadyExistsException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(AssessmentNotReadyForCompletionException.class)
    public ResponseEntity<ErrorResponse> handleAssessmentNotReadyForCompletion(AssessmentNotReadyForCompletionException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(AssessmentCooldownException.class)
    public ResponseEntity<ErrorResponse> handleAssessmentCooldown(AssessmentCooldownException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(AssessmentTemplateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAssessmentTemplateNotFound(AssessmentTemplateNotFoundException e) {
        // Это не вина пользователя: шаблон должен был существовать.
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal configuration error");
    }

    @ExceptionHandler(AssessmentTemplatePoolExhaustedException.class)
    public ResponseEntity<ErrorResponse> handleAssessmentTemplatePoolExhausted(
        AssessmentTemplatePoolExhaustedException e) {
        // Не хватает активных шаблонов под запрошенный difficulty.
        // Пользователю нечего тут делать, это проблема наполнения.
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal configuration error");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(status.value())
                .errorMessage(message)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}