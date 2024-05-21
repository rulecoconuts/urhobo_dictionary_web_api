package com.fejiro.exploration.dictionary.dictionary_web_api.service.word;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WordDomainObjectToWordDataObjectConverter implements Converter<WordDomainObject, WordDataObject> {
    @Override
    public WordDataObject convert(WordDomainObject source) {
        return WordDataObject.builder()
                             .id(source.getId())
                             .name(source.getName())
                             .languageId(source.getLanguageId())
                             .createdBy(source.getCreatedBy())
                             .updatedBy(source.getUpdatedBy())
                             .createdAt(source.getCreatedAt())
                             .updatedAt(source.getUpdatedAt())
                             .build();
    }
}
