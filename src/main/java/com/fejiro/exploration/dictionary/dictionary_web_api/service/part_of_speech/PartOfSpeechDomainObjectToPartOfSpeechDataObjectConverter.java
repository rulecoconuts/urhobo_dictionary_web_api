package com.fejiro.exploration.dictionary.dictionary_web_api.service.part_of_speech;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PartOfSpeechDomainObjectToPartOfSpeechDataObjectConverter implements Converter<PartOfSpeechDomainObject, PartOfSpeechDataObject> {
    @Override
    public PartOfSpeechDataObject convert(PartOfSpeechDomainObject source) {
        return PartOfSpeechDataObject.builder()
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
