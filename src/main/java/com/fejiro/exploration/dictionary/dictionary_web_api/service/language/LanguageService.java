package com.fejiro.exploration.dictionary.dictionary_web_api.service.language;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.CRUDService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.NameSearchableService;


public interface LanguageService extends CRUDService<LanguageDomainObject, Integer>, NameSearchableService<LanguageDomainObject> {
}
