package com.fejiro.exploration.dictionary.dictionary_web_api.service.part_of_speech;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.GenericJOOQBackedService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordDataObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Component
public class CustomJOOQBackedPartOfSpeechService implements PartOfSpeechService, GenericJOOQBackedService<PartOfSpeechDomainObject, PartOfSpeechDataObject, Integer> {
    @Autowired
    ConversionService conversionService;

    @Autowired
    CRUDDAO<PartOfSpeechDataObject, Integer> partOfSpeechDataObjectLongCRUDDAO;

    @Override
    public ConversionService getConversionService() {
        return conversionService;
    }

    @Override
    public Class<? extends PartOfSpeechDomainObject> getDomainClass() {
        return PartOfSpeechDomainObject.class;
    }

    @Override
    public Class<? extends PartOfSpeechDataObject> getDataClass() {
        return PartOfSpeechDataObject.class;
    }

    @Override
    public CRUDDAO<PartOfSpeechDataObject, Integer> getCRUDAO() {
        return partOfSpeechDataObjectLongCRUDDAO;
    }

    @Override
    public String generateErrorLabel(PartOfSpeechDomainObject model) {
        return String.format("%s_%s", model.getId(), model.getName());
    }

    @Override
    public Integer getId(PartOfSpeechDomainObject model) {
        return model.getId();
    }
}
