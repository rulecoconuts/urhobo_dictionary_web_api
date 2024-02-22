package com.fejiro.exploration.dictionary.dictionary_web_api.service.word;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.GenericJOOQBackedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Component
public class CustomJOOQBackedWordService implements WordService, GenericJOOQBackedService<WordDomainObject, WordDataObject, Long> {
    @Autowired
    ConversionService conversionService;

    @Autowired
    CRUDDAO<WordDataObject, Long> wordCRUDDAO;

    @Override
    public ConversionService getConversionService() {
        return conversionService;
    }

    @Override
    public Class<? extends WordDomainObject> getDomainClass() {
        return WordDomainObject.class;
    }

    @Override
    public Class<? extends WordDataObject> getDataClass() {
        return WordDataObject.class;
    }

    @Override
    public CRUDDAO<WordDataObject, Long> getCRUDAO() {
        return wordCRUDDAO;
    }

    @Override
    public String generateErrorLabel(WordDomainObject model) {
        return String.format("%s_%s_%s", model.getId(), model.getName(), model.getLanguageId());
    }

    @Override
    public Long getId(WordDomainObject model) {
        return model.getId();
    }
}
