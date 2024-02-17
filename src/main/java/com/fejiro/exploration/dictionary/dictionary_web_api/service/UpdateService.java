package com.fejiro.exploration.dictionary.dictionary_web_api.service;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.ApiExceptionWithComplexObjectMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface UpdateService<T> {
    /**
     * Update a model in the database
     *
     * @param model
     * @return
     */
    T update(T model) throws IllegalArgumentExceptionWithMessageMap;

    Iterable<T> updateAll(Iterable<T> models);

    Map<String, String> validateModelForUpdate(T model, Optional<T> existingCopy);

    void throwIfModelIsInvalidForUpdate(T model,
                                        Optional<T> existingCopy) throws IllegalArgumentExceptionWithMessageMap;

    Map<T, Map<String, String>> validateModelsForUpdate(Iterable<T> models, Iterable<T> existingCopies);

    void throwIfModelsAreInvalidForUpdate(Iterable<T> models,
                                          Iterable<T> existingCopies) throws ApiExceptionWithComplexObjectMessageMap;
}
