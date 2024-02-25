package com.fejiro.exploration.dictionary.dictionary_web_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NameSearchableService<T> {
    Page<T> searchByName(String namePattern, Pageable pageable);
}
