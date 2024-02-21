package com.fejiro.exploration.dictionary.dictionary_web_api.service.language;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.GenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.GenericJOOQBackedService;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Language service that uses JOOQ to handle data layer operations.
 * TODO: Implement methods
 */
@Component
public class CustomJOOQBackedLanguageService implements LanguageService, GenericJOOQBackedService<LanguageDomainObject, LanguageDataObject, Integer> {
    @Autowired
    ConversionService conversionService;

    @Autowired
    CRUDDAO<LanguageDataObject, Integer> languageCRUDDAO;

    @Override
    public ConversionService getConversionService() {
        return conversionService;
    }

    @Override
    public Class<? extends LanguageDomainObject> getDomainClass() {
        return LanguageDomainObject.class;
    }

    @Override
    public Class<? extends LanguageDataObject> getDataClass() {
        return LanguageDataObject.class;
    }

    @Override
    public CRUDDAO<LanguageDataObject, Integer> getCRUDAO() {
        return languageCRUDDAO;
    }

    @Override
    public String generateErrorLabel(LanguageDomainObject model) {
        return String.format("%s_%s", model.getId(), model.getName());
    }

    @Override
    public Page<LanguageDomainObject> searchByName(String namePattern, Pageable pageable) {
        return ((GenericJOOQCRUDDAO<LanguageDataObject, Integer, ?>) languageCRUDDAO)
                .retrieveAll(Language.LANGUAGE.NAME.likeIgnoreCase(namePattern), pageable)
                .map(this::toDomain);
    }

    @Override
    public Map<String, String> validateModelForCreation(LanguageDomainObject model) {

        Map<String, String> errors = new HashMap<>();

        if (model.getName() == null || model.getName().isBlank()) {
            errors.put("name", "Language name is required");
        } else {
            var existing = retrieveOne(Language.LANGUAGE.NAME.equalIgnoreCase(model.getName()));

            if (existing.isPresent()) errors.put("name", "Language name already exists");
        }

        if (model.getId() != null) {
            errors.put("id", "ID must be null for new language");
        }

        return errors;
    }

    @Override
    public Map<String, String> validateModelForUpdate(LanguageDomainObject model,
                                                      Optional<LanguageDomainObject> existingCopy) {
        Map<String, String> errors = new HashMap<>();

        if (model.getName() == null || model.getName().isBlank()) {
            errors.put("name", "Language name is required");
        } else {
            var existing = retrieveOne(Language.LANGUAGE.NAME.equalIgnoreCase(model.getName()));

            if (existing.isPresent() && model.getId() != null && !existing.get().getId().equals(model.getId()))
                errors.put("name", "Language name already exists");
        }

        if (model.getId() == null) {
            errors.put("id", "ID is required to update a language");
        } else if (model.getId() < 1) {
            errors.put("id", "ID must be greater than 0");
        } else if (existingCopy.isEmpty()) {
            errors.put("id", "ID does not exist in database");
        }

        return errors;
    }

    @Override
    public Integer getId(LanguageDomainObject model) {
        return model.getId();
    }

    @Override
    public LanguageDomainObject preProcessBeforeUpdate(LanguageDomainObject model,
                                                       Optional<LanguageDomainObject> existingCopy) {
        LanguageDomainObject newModel = model.toBuilder().build();
        newModel.setCreatedAt(existingCopy.get().getCreatedAt());
        newModel.setCreatedBy(existingCopy.get().getCreatedBy());
        return newModel;
    }
}
