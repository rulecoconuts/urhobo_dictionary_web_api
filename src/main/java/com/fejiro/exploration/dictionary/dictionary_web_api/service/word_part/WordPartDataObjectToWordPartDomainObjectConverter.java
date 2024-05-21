package com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WordPartDataObjectToWordPartDomainObjectConverter implements Converter<WordPartDataObject, WordPartDomainObject> {
    @Override
    public WordPartDomainObject convert(WordPartDataObject source) {
        return WordPartDomainObject.builder()
                                   .id(source.getId())
                                   .wordId(source.getWordId())
                                   .partId(source.getPartId())
                                   .definition(source.getDefinition())
                                   .note(source.getNote())
                                   .createdBy(source.getCreatedBy())
                                   .updatedBy(source.getUpdatedBy())
                                   .createdAt(source.getCreatedAt())
                                   .updatedAt(source.getUpdatedAt())
                                   .build();
    }
}
