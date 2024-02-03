package com.fejiro.exploration.dictionary.dictionary_web_api.error_handling;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackages = "com.fejiro.exploration.dictionary.dictionary_web_api.controller.rest")
public class RESTExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiExceptionWithMessageMap.class)
    protected ResponseEntity handleExceptionWithMessageMapException(
            ApiExceptionWithMessageMap exceptionWithMessageMap) {
        ApiError error = ApiError.builder()
                                 .status(exceptionWithMessageMap.getStatus())
                                 .message(exceptionWithMessageMap.getMessage())
                                 .timestamp(exceptionWithMessageMap.getTimeStamp())
                                 .errorMessages(exceptionWithMessageMap.getMessageMap())
                                 .build();

        return convertApiErrorToResponseEntity(error);
    }

    ResponseEntity convertApiErrorToResponseEntity(ApiError error) {
        return ResponseEntity.status(error.getStatus()).body(error);
    }
}
