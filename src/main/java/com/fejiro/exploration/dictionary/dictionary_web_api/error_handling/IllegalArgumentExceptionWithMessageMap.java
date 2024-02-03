package com.fejiro.exploration.dictionary.dictionary_web_api.error_handling;

import java.util.Map;

public class IllegalArgumentExceptionWithMessageMap extends ApiExceptionWithMessageMap {


    public IllegalArgumentExceptionWithMessageMap(String summaryMessage, Map<String, String> messageMap) {
        super(summaryMessage, messageMap);
    }
}
