package com.fejiro.exploration.dictionary.dictionary_web_api.data_transfer.word;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;

import java.util.Map;

public interface WordCreationRequestDTOValidator {
    void throwIfInvalid(WordCreationRequestDTO creationRequestDTO) throws IllegalArgumentExceptionWithMessageMap;

    Map<String, String> validate(WordCreationRequestDTO creationRequestDTO);
}
