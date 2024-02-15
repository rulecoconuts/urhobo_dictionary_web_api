package com.fejiro.exploration.dictionary.dictionary_web_api.service;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;

import java.util.Map;

public interface UpdateService<T> {
    /**
     * Update a model in the database
     *
     * @param model
     * @return
     */
    T update(T model);

    Iterable<T> updateAll(Iterable<T> models);

    Map<String, String> validateModelForUpdate(T model);

    void throwIfModelIsInvalidForUpdate(T model) throws IllegalArgumentExceptionWithMessageMap;
}
