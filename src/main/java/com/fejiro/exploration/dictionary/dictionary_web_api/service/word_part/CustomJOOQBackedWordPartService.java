package com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.GenericJOOQBackedService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordDataObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Component
public class CustomJOOQBackedWordPartService implements WordPartService, GenericJOOQBackedService<WordPartDomainObject, WordPartDataObject, Long> {

    @Autowired
    ConversionService conversionService;

    @Autowired
    CRUDDAO<WordPartDataObject, Long> wordPartDataObjectLongCRUDDAO;

    @Override
    public ConversionService getConversionService() {
        return conversionService;
    }

    @Override
    public Class<? extends WordPartDomainObject> getDomainClass() {
        return WordPartDomainObject.class;
    }

    @Override
    public Class<? extends WordPartDataObject> getDataClass() {
        return WordPartDataObject.class;
    }

    @Override
    public CRUDDAO<WordPartDataObject, Long> getCRUDAO() {
        return wordPartDataObjectLongCRUDDAO;
    }

    @Override
    public String generateErrorLabel(WordPartDomainObject model) {
        return String.format("%s_%s_%s", model.getId(), model.getWordId(), model.getPartId());
    }

    @Override
    public Long getId(WordPartDomainObject model) {
        return model.getId();
    }
}
