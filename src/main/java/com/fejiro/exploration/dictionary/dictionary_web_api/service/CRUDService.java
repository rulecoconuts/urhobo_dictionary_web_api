package com.fejiro.exploration.dictionary.dictionary_web_api.service;

import com.fejiro.exploration.dictionary.dictionary_web_api.error_handling.IllegalArgumentExceptionWithMessageMap;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface CRUDService<T, I> extends CreationService<T>, RetrievalService<T, I>, DeletionService<T, I>, UpdateService<T> {

}
