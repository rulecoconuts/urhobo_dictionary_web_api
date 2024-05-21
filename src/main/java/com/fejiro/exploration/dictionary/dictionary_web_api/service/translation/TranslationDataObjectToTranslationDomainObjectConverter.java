package com.fejiro.exploration.dictionary.dictionary_web_api.service.translation;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TranslationDataObjectToTranslationDomainObjectConverter implements Converter<TranslationDataObject, TranslationDomainObject> {
    @Override
    public TranslationDomainObject convert(TranslationDataObject source) {
        return TranslationDomainObject.builder()
                                      .id(source.getId())
                                      .sourceWordPartId(source.getSourceWordPartId())
                                      .targetWordPartId(source.getTargetWordPartId())
                                      .note(source.getNote())
                                      .reverseNote(source.getReverseNote())
                                      .createdBy(source.getCreatedBy())
                                      .updatedBy(source.getUpdatedBy())
                                      .createdAt(source.getCreatedAt())
                                      .updatedAt(source.getUpdatedAt())
                                      .build();
    }
}
