package com.fejiro.exploration.dictionary.dictionary_web_api.service.translation;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TranslationDomainObjectToTranslationDataObjectConverter implements Converter<TranslationDomainObject, TranslationDataObject> {
    @Override
    public TranslationDataObject convert(TranslationDomainObject source) {
        return TranslationDataObject.builder()
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
