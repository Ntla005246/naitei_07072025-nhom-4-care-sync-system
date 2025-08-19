package com.example.backend.exception;

import com.example.backend.dto.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    private String toExternalCode(String internalKey) {
        if (internalKey == null)
            return null;
        return internalKey.replace('.', '_').toUpperCase();
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        log.warn("Business exception: Code - {}, Message - {}", ex.getErrorCode(), ex.getMessage());
        String localizedMessage = getMessage(ex.getErrorCode(), ex.getArgs());
        return ResponseEntity.badRequest().body(ApiResponse.error(toExternalCode(ex.getErrorCode()),
                localizedMessage, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        log.warn("Resource not found exception: Code - {}, Message - {}", ex.getErrorCode(),
                ex.getMessage());
        String localizedMessage = getMessage(ex.getErrorCode(), ex.getArgs());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(
                toExternalCode(ex.getErrorCode()), localizedMessage, HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedException(UnauthorizedException ex) {
        log.warn("Unauthorized exception: Code - {}", ex.getErrorCode());
        String localizedMessage = getMessage(ex.getErrorCode(), ex.getArgs());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(toExternalCode(ex.getErrorCode()), localizedMessage,
                        HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex) {
        log.warn("User already exists exception: Code - {}", ex.getErrorCode());
        String localizedMessage = getMessage(ex.getErrorCode(), ex.getArgs());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(
                toExternalCode(ex.getErrorCode()), localizedMessage, HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                String fieldName = fieldError.getField();
                String errorMessage = error.getDefaultMessage();
                Object rejectedValue = fieldError.getRejectedValue();
                String code = fieldError.getCode();

                Map<String, Object> fieldDetails = new HashMap<>();
                fieldDetails.put("message", errorMessage);
                fieldDetails.put("rejectedValue", rejectedValue);
                if (code != null) {
                    fieldDetails.put("validationType", code);
                }
                errors.put(fieldName, fieldDetails);
            } else {
                String errorMessage = error.getDefaultMessage();
                errors.put(error.getObjectName(), errorMessage);
            }
        });
        log.warn("Validation exception: {}", errors);
        String localizedMessage = getMessage("error.validation");
        return ResponseEntity.badRequest().body(ApiResponse.error("VALIDATION_ERROR",
                localizedMessage, errors, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        String localizedMessage = getMessage("error.access.denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponse.error("ACCESS_DENIED", localizedMessage, HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {
        log.warn("HTTP method not supported: {}", ex.getMessage());
        String localizedMessage = getMessage("error.method.not.supported");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ApiResponse.error(
                "METHOD_NOT_ALLOWED", localizedMessage, HttpStatus.METHOD_NOT_ALLOWED.value()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        log.warn("Malformed JSON request: {}", ex.getMessage());
        String localizedMessage = getMessage("error.malformed.json");
        return ResponseEntity.badRequest().body(ApiResponse.error("MALFORMED_JSON",
                localizedMessage, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMessage());
        String localizedMessage = getMessage("error.data.integrity.violation");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse
                .error("DATA_INTEGRITY_VIOLATION", localizedMessage, HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String field = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);

            Map<String, Object> fieldDetails = new HashMap<>();
            fieldDetails.put("message", violation.getMessage());
            fieldDetails.put("rejectedValue", violation.getInvalidValue());
            fieldDetails.put("constraintType", violation.getConstraintDescriptor().getAnnotation()
                    .annotationType().getSimpleName());

            errors.put(field, fieldDetails);
        });
        log.warn("Constraint violation: {}", errors);
        String localizedMessage = getMessage("error.validation");
        return ResponseEntity.badRequest().body(ApiResponse.error("VALIDATION_ERROR",
                localizedMessage, errors, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        String fieldName = ex.getName();
        String expectedType = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "unknown";
        log.warn("Type mismatch: Field {} should be of type {}", fieldName, expectedType);
        String localizedMessage = getMessage("error.type.mismatch", fieldName, expectedType);

        Map<String, Object> details = new HashMap<>();
        details.put("parameterName", ex.getName());
        details.put("providedValue", ex.getValue());
        details.put("requiredType", expectedType);

        return ResponseEntity.badRequest().body(ApiResponse.error("TYPE_MISMATCH_ERROR",
                localizedMessage, details, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingPathVariableException(
            MissingPathVariableException ex) {
        log.warn("Missing path variable: {}", ex.getVariableName());
        String localizedMessage = getMessage("error.path.variable.missing", ex.getVariableName());
        return ResponseEntity.badRequest().body(ApiResponse.error("PATH_VARIABLE_MISSING",
                localizedMessage, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getParameterName());
        String localizedMessage = getMessage("error.parameter.missing", ex.getParameterName());

        Map<String, Object> details = new HashMap<>();
        details.put("parameterName", ex.getParameterName());
        details.put("parameterType", ex.getParameterType());

        return ResponseEntity.badRequest().body(ApiResponse.error("PARAMETER_MISSING",
                localizedMessage, details, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(
            NoHandlerFoundException ex) {
        log.warn("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());
        String localizedMessage = getMessage("error.endpoint.not.found", ex.getHttpMethod(),
                ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse
                .error("ENDPOINT_NOT_FOUND", localizedMessage, HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        String localizedMessage = getMessage("error.internal.server");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_SERVER_ERROR", localizedMessage,
                        HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
