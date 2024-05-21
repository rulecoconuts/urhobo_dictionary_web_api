package com.fejiro.exploration.dictionary.dictionary_web_api.service.translation;

import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part.WordPartDomainObject;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FullTranslation {
    @EqualsAndHashCode.Include
    TranslationDomainObject translation;

    WordDomainObject sourceWord;
    WordDomainObject targetWord;

    WordPartDomainObject sourceWordPart;

    WordPartDomainObject targetWordPart;
}
