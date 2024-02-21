package com.fejiro.exploration.dictionary.dictionary_web_api.service;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.ApiExceptionWithComplexObjectMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDomainObject;

import java.util.Map;

public interface CreationService<T> {
    /**
     * Create a model in the database
     *
     * @param model Model to be created
     * @return a representation of what the model looks like in the database
     */
    T create(T model) throws IllegalArgumentExceptionWithMessageMap;

    Iterable<T> createAll(
            Iterable<T> models) throws IllegalArgumentExceptionWithMessageMap, ApiExceptionWithComplexObjectMessageMap;

    Map<String, String> validateModelForCreation(T model);


    void throwIfModelIsInvalidForCreation(T model) throws IllegalArgumentExceptionWithMessageMap;

    Map<T, Map<String, String>> validateModelsForCreation(Iterable<T> models);

    void throwIfModelsAreInvalidForCreation(Iterable<T> models) throws ApiExceptionWithComplexObjectMessageMap;
}
