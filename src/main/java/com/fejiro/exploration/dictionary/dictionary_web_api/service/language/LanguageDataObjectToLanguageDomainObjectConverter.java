package com.fejiro.exploration.dictionary.dictionary_web_api.service.language;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LanguageDataObjectToLanguageDomainObjectConverter implements Converter<LanguageDataObject, LanguageDomainObject> {
    @Override
    public LanguageDomainObject convert(LanguageDataObject source) {
        return LanguageDomainObject.builder()
                                   .id(source.getId())
                                   .name(source.getName())
                                   .description(source.getDescription())
                                   .createdBy(source.getCreatedBy())
                                   .updatedBy(source.getUpdatedBy())
                                   .createdAt(source.getCreatedAt())
                                   .updatedAt(source.getUpdatedAt())
                                   .build();
    }
}
