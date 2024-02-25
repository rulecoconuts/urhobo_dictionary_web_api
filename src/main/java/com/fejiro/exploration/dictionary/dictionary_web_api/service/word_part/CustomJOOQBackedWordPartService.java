package com.fejiro.exploration.dictionary.dictionary_web_api.service.word_part;

import com.fejiro.exploration.dictionary.dictionary_web_api.database.CRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.database.GenericJOOQCRUDDAO;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.GenericJOOQBackedService;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.part_of_speech.PartOfSpeechDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordDataObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.word.WordDomainObject;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.PartOfSpeech;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.Word;
import com.fejiro.exploration.dictionary.dictionary_web_api.tables.WordPart;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @Override
    public Map<String, String> validateModelForCreation(WordPartDomainObject model) {
        Map<String, String> errors = new HashMap<>();

        if (model.getId() != null) {
            errors.put("id", "ID must be null for a newly created word part");
        }

        if (model.getWordId() == null) {
            errors.put("word", "Word is required for word part");
        }

        if (model.getPartId() == null) {
            errors.put("part", "Part of speech is required for word part");
        }

        if (!errors.containsKey("word") && !errors.containsKey("part")) {
            Optional<WordPartDomainObject> existingMatch
                    = retrieveOne(
                    DSL.and(
                            WordPart.WORD_PART.WORD_ID
                                    .eq(model.getWordId()),
                            WordPart.WORD_PART.PART_ID
                                    .eq(model.getPartId())
                    )
            );

            if (existingMatch.isPresent()) {
                errors.put("duplicate", "Word part is not unique");
            }
        }

        return errors;
    }

    @Override
    public Map<String, String> validateModelForUpdate(WordPartDomainObject model,
                                                      Optional<WordPartDomainObject> existingCopy) {
        Map<String, String> errors = new HashMap<>();

        if (model.getId() == null) {
            errors.put("id", "ID must be non-null to update word part");
        }

        if (model.getWordId() == null) {
            errors.put("word", "Word is required for word part");
        } else if (existingCopy.isPresent() && !existingCopy.get().getWordId().equals(model.getWordId())) {
            errors.put("word", "Word in word part cannot be changed");
        }

        if (model.getPartId() == null) {
            errors.put("part", "Part of speech is required for word part");
        }

        if (!errors.containsKey("word") && !errors.containsKey("part")
                && existingCopy.isPresent() && !existingCopy.get().getPartId().equals(model.getPartId())) {
            // Part has been changed, check if new word part pair is unique
            Optional<WordPartDomainObject> existingMatch
                    = retrieveOne(
                    DSL.and(
                            WordPart.WORD_PART.WORD_ID
                                    .eq(model.getWordId()),
                            WordPart.WORD_PART.PART_ID
                                    .eq(model.getPartId())
                    )
            );

            if (existingMatch.isPresent()) {
                errors.put("duplicate", "Word part is not unique");
            }
        }

        if (existingCopy.isEmpty()) {
            errors.put("id", "Word part with id does not exist");
        }

        return errors;
    }
}
