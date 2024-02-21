package com.fejiro.exploration.dictionary.dictionary_web_api.service.language;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.CRUDService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface LanguageService extends CRUDService<LanguageDomainObject, Integer> {
    Page<LanguageDomainObject> searchByName(String namePattern, Pageable pageable);
}
