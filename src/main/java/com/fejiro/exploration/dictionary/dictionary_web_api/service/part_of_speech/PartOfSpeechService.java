package com.fejiro.exploration.dictionary.dictionary_web_api.service.part_of_speech;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.CRUDService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.NameSearchableService;

public interface PartOfSpeechService extends CRUDService<PartOfSpeechDomainObject, Integer>, NameSearchableService<PartOfSpeechDomainObject> {
}
