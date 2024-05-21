package com.fejiro.exploration.dictionary.dictionary_web_api.service.word;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.CRUDService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.NameSearchableService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.language.LanguageDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation.PronunciationDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.FullWordPartDomainObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WordService extends CRUDService<WordDomainObject, Long>, NameSearchableService<WordDomainObject> {
    Page<FullWordPartDomainObject> searchForFullWordPartByName(String namePattern, Pageable pageable);

    Page<FullWordPartDomainObject> searchByNameFullInLanguage(String namePattern, LanguageDomainObject language,
                                                              Pageable pageable);

}
