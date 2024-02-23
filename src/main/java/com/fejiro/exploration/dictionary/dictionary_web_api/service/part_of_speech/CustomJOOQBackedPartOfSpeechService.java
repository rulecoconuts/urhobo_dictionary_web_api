package com.fejiro.exploration.dictionary.dictionary_web_api.service.part_of_speech;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.GenericJOOQBackedService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.PartOfSpeech;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @Override
    public Map<String, String> validateModelForCreation(PartOfSpeechDomainObject model) {
        Map<String, String> errors = new HashMap<>();

        if (model.getId() != null) {
            errors.put("id", "ID must be null for newly created part of speech ");
        }

        if (model.getName() == null) {
            errors.put("name", "Part of speech name is required");
        } else if (model.getName().isBlank()) {
            errors.put("name", "Part of speech name cannot be blank");
        } else {
            // Check if name is unique
            Optional<PartOfSpeechDomainObject> existingWithName
                    = retrieveOne(PartOfSpeech.PART_OF_SPEECH.NAME.equalIgnoreCase(model.getName()));
            if (existingWithName.isPresent()) {
                errors.put("name", "Part of speech name is not unique");
            }
        }


        return errors;
    }

    @Override
    public Map<String, String> validateModelForUpdate(PartOfSpeechDomainObject model,
                                                      Optional<PartOfSpeechDomainObject> existingCopy) {
        Map<String, String> errors = new HashMap<>();

        if (model.getId() == null) {
            errors.put("id", "ID must be non-null to update part of speech");
        }

        if (model.getName() == null) {
            errors.put("name", "Part of speech name is required");
        } else if (model.getName().isBlank()) {
            errors.put("name", "Part of speech name cannot be blank");
        } else if (existingCopy.isPresent() && !model.getName()
                                                     .toLowerCase()
                                                     .equals(existingCopy.get().getName().toLowerCase())) {

            // If name has been changed, check if new name is unique
            Optional<PartOfSpeechDomainObject> existingWithName
                    = retrieveOne(PartOfSpeech.PART_OF_SPEECH.NAME.equalIgnoreCase(model.getName()));
            if (existingWithName.isPresent()) {
                errors.put("name", "Part of speech name is not unique");
            }
        }

        if (existingCopy.isEmpty()) {
            errors.put("id", "ID does not exist for parts of speech");
        }

        return errors;
    }
}
