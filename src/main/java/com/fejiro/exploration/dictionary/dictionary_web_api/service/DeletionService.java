package com.fejiro.exploration.dictionary.dictionary_web_api.service;

public interface DeletionService<T, I> {
    void delete(T model);

    void deleteById(I id);

    void deleteAll(Iterable<T> models);

    void deleteAllById(Iterable<I> ids);
}
