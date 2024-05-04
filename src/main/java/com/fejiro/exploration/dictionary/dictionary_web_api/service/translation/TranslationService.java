package com.fejiro.exploration.dictionary.dictionary_web_api.service.translation;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.CRUDService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.language.LanguageDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.WordPartDomainObject;

public interface TranslationService extends CRUDService<TranslationDomainObject, Long> {
    /**
     * Fetch translations that contain the word part as a source or target
     *
     * @param wordPart
     * @return
     */
    Iterable<FullTranslation> fetchTranslations(WordPartDomainObject wordPart, LanguageDomainObject targetLanguage);
}
