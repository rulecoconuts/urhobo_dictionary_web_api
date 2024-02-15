package com.fejiro.exploration.dictionary.dictionary_web_api.service;

import java.util.Optional;

public interface RetrievalService<T, I> {
    Optional<T> retrieveById(I id);

    Iterable<T> retrieveAll();

    Iterable<T> retrieveAllById(Iterable<I> ids);
}
