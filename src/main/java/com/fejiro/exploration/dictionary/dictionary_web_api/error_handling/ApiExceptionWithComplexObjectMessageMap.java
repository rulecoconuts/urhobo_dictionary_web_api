package com.fejiro.exploration.dictionary.dictionary_web_api.error_handling;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class ApiExceptionWithComplexObjectMessageMap extends Exception {
    HttpStatus status;
    OffsetDateTime timeStamp;

    final Map<Object, Map<String, String>> messageMap = new HashMap<>();

    Function<Object, String> labelGenerator;

    public ApiExceptionWithComplexObjectMessageMap() {
        setTimeToNow();
    }

    public ApiExceptionWithComplexObjectMessageMap(String message) {
        super(message);
        setTimeToNow();
    }

    public ApiExceptionWithComplexObjectMessageMap(String message, Throwable cause) {
        super(message, cause);
        setTimeToNow();
    }

    public ApiExceptionWithComplexObjectMessageMap(Throwable cause) {
        super(cause);
        setTimeToNow();
    }

    public ApiExceptionWithComplexObjectMessageMap(String message, Throwable cause, boolean enableSuppression,
                                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        setTimeToNow();
    }

    public ApiExceptionWithComplexObjectMessageMap(String summaryMessage, HttpStatus status,
                                                   Map<?, Map<String, String>> messageMap,
                                                   Function<Object, String> labelGenerator) {

        super(summaryMessage);
        this.status = status;
        this.messageMap.putAll(messageMap);
        this.labelGenerator = labelGenerator;

        setTimeToNow();
    }

    public ApiExceptionWithComplexObjectMessageMap(HttpStatus status, Map<Object, Map<String, String>> messageMap,
                                                   Function<Object, String> labelGenerator) {
        this.status = status;
        this.messageMap.putAll(messageMap);
        this.labelGenerator = labelGenerator;
        setTimeToNow();
    }

    void setTimeToNow() {
        timeStamp = OffsetDateTime.now();
    }

    public Map<Object, Map<String, String>> getMessageMap() {
        return Collections.unmodifiableMap(messageMap);
    }

    public Map<String, String> getCompressedMessageMap() {
        return getMessageMap()
                .entrySet()
                .stream()
                .map((entry) -> Map.entry(labelGenerator.apply(entry.getKey()),
                                          compressSubMessageMap(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    String compressSubMessageMap(Map<String, String> map) {
        return map.entrySet().stream().map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                  .collect(Collectors.joining("|--|"));
    }
}
