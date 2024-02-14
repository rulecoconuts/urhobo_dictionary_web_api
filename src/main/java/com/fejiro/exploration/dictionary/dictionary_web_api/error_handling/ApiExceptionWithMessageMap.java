package com.fejiro.exploration.dictionary.dictionary_web_api.error_handling;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ApiExceptionWithMessageMap extends Exception {
    HttpStatus status;
    OffsetDateTime timeStamp;

    final Map<String, String> messageMap = new HashMap<>();

    public ApiExceptionWithMessageMap() {
        setTimeToNow();
    }

    public ApiExceptionWithMessageMap(String message) {
        super(message);
        setTimeToNow();
    }

    public ApiExceptionWithMessageMap(String message, Throwable cause) {
        super(message, cause);
        setTimeToNow();
    }

    public ApiExceptionWithMessageMap(Throwable cause) {
        super(cause);
        setTimeToNow();
    }

    public ApiExceptionWithMessageMap(String message, Throwable cause, boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        setTimeToNow();
    }

    public ApiExceptionWithMessageMap(String summaryMessage, Map<String, String> messageMap) {
        super(summaryMessage);
        this.messageMap.putAll(messageMap);
        setTimeToNow();
    }

    public ApiExceptionWithMessageMap(Map<String, String> messageMap) {
        this.messageMap.putAll(messageMap);
        setTimeToNow();
    }

    void setTimeToNow() {
        timeStamp = OffsetDateTime.now();
    }


    public Map<String, String> getMessageMap() {
        return Collections.unmodifiableMap(messageMap);
    }
}
