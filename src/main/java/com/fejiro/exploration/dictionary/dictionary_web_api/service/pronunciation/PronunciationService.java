package com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.CRUDService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.WordPartDomainObject;

public interface PronunciationService extends CRUDService<PronunciationDomainObject, Long> {
    Iterable<PronunciationDomainObject> getPronunciationsOfWordPart(WordPartDomainObject wordPart);
}
