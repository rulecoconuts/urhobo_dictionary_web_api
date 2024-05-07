package com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.CRUDService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.WordPartDomainObject;

import java.util.List;

public interface PronunciationService extends CRUDService<PronunciationDomainObject, Long> {
    Iterable<PronunciationDomainObject> getPronunciationsOfWordPart(WordPartDomainObject wordPart);

    Iterable<PronunciationDomainObject> getPronunciationsOfWord(WordDomainObject word);

}
