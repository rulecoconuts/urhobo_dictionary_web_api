package com.fejiro.exploration.dictionary.dictionary_web_api.service.word;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.CRUDService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.NameSearchableService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.FullWordPartDomainObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WordService extends CRUDService<WordDomainObject, Long>, NameSearchableService<WordDomainObject> {
    Page<FullWordPartDomainObject> searchForFullWordPartByName(String namePattern, Pageable pageable);
    
}
