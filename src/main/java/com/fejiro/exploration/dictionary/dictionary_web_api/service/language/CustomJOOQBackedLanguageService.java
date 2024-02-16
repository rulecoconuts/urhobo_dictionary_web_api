package com.fejiro.exploration.dictionary.dictionary_web_api.service.language;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.GenericJOOQBackedService;
import org.springframework.core.convert.ConversionService;

/**
 * Language service that uses JOOQ to handle data layer operations.
 * TODO: Implement methods
 */
public class CustomJOOQBackedLanguageService implements LanguageService, GenericJOOQBackedService<LanguageDomainObject, LanguageDataObject, Integer> {
    @Override
    public ConversionService getConversionService() {
        return null;
    }

    @Override
    public Class<? extends LanguageDomainObject> getDomainClass() {
        return null;
    }

    @Override
    public Class<? extends LanguageDataObject> getDataClass() {
        return null;
    }

    @Override
    public CRUDDAO<LanguageDataObject, Integer> getCRUDAO() {
        return null;
    }

    @Override
    public String generateErrorLabel(LanguageDomainObject model) {
        return null;
    }
}
