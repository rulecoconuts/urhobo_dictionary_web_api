package com.fejiro.exploration.dictionary.dictionary_web_api.service;

/**
 * A service that uses the data layer in its core operations and converts that data to a usable format for the
 * domain layer
 *
 * @param <T> Domain object
 * @param <D> Data object
 */
public interface DataBackedService<T, D> {
    D toData(T domain);

    T toDomain(D data);
}
