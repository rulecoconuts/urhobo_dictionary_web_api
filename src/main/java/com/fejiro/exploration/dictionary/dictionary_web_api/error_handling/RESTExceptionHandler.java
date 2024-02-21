package com.fejiro.exploration.dictionary.dictionary_web_api.error_handling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

@RestControllerAdvice(basePackages = "com.fejiro.exploration.dictionary.dictionary_web_api.controller.rest")
public class RESTExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiExceptionWithMessageMap.class)
    protected ResponseEntity<Object> handleExceptionWithMessageMapException(
            ApiExceptionWithMessageMap exceptionWithMessageMap) {
        ApiError error = ApiError.builder()
                                 .status(Optional.ofNullable(exceptionWithMessageMap.getStatus())
                                                 .orElse(HttpStatus.INTERNAL_SERVER_ERROR))
                                 .message(exceptionWithMessageMap.getMessage())
                                 .timestamp(exceptionWithMessageMap.getTimeStamp())
                                 .errorMessages(exceptionWithMessageMap.getMessageMap())
                                 .build();

        var responseEntity = convertApiErrorToResponseEntity(error);

        return responseEntity;
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Object> handleAuthenticationException(AuthenticationException authenticationException) {
        ApiError error = ApiError.builder()
                                 .status(HttpStatus.FORBIDDEN)
                                 .message(authenticationException.getMessage())
                                 .timestamp(OffsetDateTime.now())
                                 .build();

        return convertApiErrorToResponseEntity(error);
    }

    /**
     * Handle non-specified uncaught exceptions
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    ResponseEntity<Object> handleAllUncaughtExceptions(Exception exception) {
        ApiError error = ApiError.builder()
                                 .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .message(exception.getMessage())
                                 .timestamp(OffsetDateTime.now())
                                 .build();

        return convertApiErrorToResponseEntity(error);
    }

    ResponseEntity<Object> convertApiErrorToResponseEntity(ApiError error) {
        return ResponseEntity.status(error.getStatus())
                             .body(error);
    }
}
