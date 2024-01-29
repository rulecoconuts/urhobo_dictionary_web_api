package com.fejiro.exploration.dictionary.dictionary_web_api.error_handling;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IllegalArgumentExceptionWithMessageMap extends IllegalArgumentException implements ExceptionWithMessageMap {
    final Map<String, String> messageMap = new HashMap<>();

    public IllegalArgumentExceptionWithMessageMap(String summaryMessage, Map<String, String> messageMap) {
        super(summaryMessage);
        this.messageMap.putAll(messageMap);
    }

    public IllegalArgumentExceptionWithMessageMap(Map<String, String> messageMap) {
        this.messageMap.putAll(messageMap);
    }


    @Override
    public Map<String, String> getMessageMap() {
        return Collections.unmodifiableMap(messageMap);
    }
}
