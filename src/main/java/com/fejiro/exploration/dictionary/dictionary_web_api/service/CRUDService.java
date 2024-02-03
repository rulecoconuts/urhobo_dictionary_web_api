package com.fejiro.exploration.dictionary.dictionary_web_api.service;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface CRUDService<T, I> {
    /**
     * Create a model in the database
     *
     * @param model Model to be created
     * @return a representation of what the model looks like in the database
     */
    T create(T model) throws IllegalArgumentExceptionWithMessageMap;

    Iterable<T> createAll(Iterable<T> models) throws IllegalArgumentExceptionWithMessageMap;

    /**
     * Update a model in the database
     *
     * @param model
     * @return
     */
    T update(T model);

    Iterable<T> updateAll(Iterable<T> models);

    Optional<T> retrieveById(I id);

    Iterable<T> retrieveAll();

    Iterable<T> retrieveAllById(Iterable<I> ids);

    void delete(T model);

    void deleteById(I id);

    void deleteAll(Iterable<T> models);

    void deleteAllById(Iterable<I> ids);
}
