package com.fejiro.exploration.dictionary.dictionary_web_api.service.language;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LanguageDomainObjectToLanguageDataObjectConverter implements Converter<LanguageDomainObject, LanguageDataObject> {
    @Override
    public LanguageDataObject convert(LanguageDomainObject source) {
        return LanguageDataObject.builder()
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
