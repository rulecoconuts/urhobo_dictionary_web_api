package com.fejiro.exploration.dictionary.dictionary_web_api.service.pronunciation;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PronunciationDataObjectToPronunciationDomainObjectConverter implements Converter<PronunciationDataObject, PronunciationDomainObject> {
    @Override
    public PronunciationDomainObject convert(PronunciationDataObject source) {
        return PronunciationDomainObject.builder()
                                        .id(source.getId())
                                        .phoneticSpelling(source.getPhoneticSpelling())
                                        .audioUrl(source.getAudioUrl())
                                        .audioByteSize(source.getAudioByteSize())
                                        .audioFileType(source.getAudioFileType())
                                        .audioMillisecondDuration(source.getAudioMillisecondDuration())
                                        .wordPartId(source.getWordPartId())
                                        .createdBy(source.getCreatedBy())
                                        .updatedBy(source.getUpdatedBy())
                                        .createdAt(source.getCreatedAt())
                                        .updatedAt(source.getUpdatedAt())
                                        .build();
    }
}
