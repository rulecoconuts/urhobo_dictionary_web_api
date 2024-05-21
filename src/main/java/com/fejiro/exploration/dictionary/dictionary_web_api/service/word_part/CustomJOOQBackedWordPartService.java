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
import org.jooq.Condition;
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
import java.util.stream.StreamSupport;

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

        performSharedModelValidationForCreation(model, errors);

        if (!errors.containsKey("word") && !errors.containsKey("part")) {
            Optional<WordPartDomainObject> existingMatch
                    = retrieveOne(
                    getMatchCondition(model)
            );

            if (existingMatch.isPresent()) {
                errors.put("duplicate", "Word part is not unique");
            }
        }

        return errors;
    }

    Condition getMatchCondition(WordPartDomainObject model) {
        return DSL.and(
                WordPart.WORD_PART.WORD_ID
                        .eq(model.getWordId()),
                WordPart.WORD_PART.PART_ID
                        .eq(model.getPartId())
        );
    }

    private static void performSharedModelValidationForCreation(WordPartDomainObject model,
                                                                Map<String, String> errors) {
        if (model.getId() != null) {
            errors.put("id", "ID must be null for a newly created word part");
        }

        if (model.getWordId() == null) {
            errors.put("word", "Word is required for word part");
        }

        if (model.getPartId() == null) {
            errors.put("part", "Part of speech is required for word part");
        }
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

    /**
     * Validate multiple models before creation
     *
     * @param models
     * @return
     */
    @Override
    public Map<WordPartDomainObject, Map<String, String>> validateModelsForCreation(
            Iterable<WordPartDomainObject> models) {
        Map<WordPartDomainObject, Map<String, String>> errors = new HashMap<>();
        // Perform shared validation for all models
        for (var model : models) {
            Map<String, String> modelSpecificErrors = new HashMap<>();
            performSharedModelValidationForCreation(model, modelSpecificErrors);

            if (!modelSpecificErrors.isEmpty()) {
                errors.put(model, modelSpecificErrors);
            }
        }

        var modelList = StreamSupport.stream(models.spliterator(), false)
                                     .toList();

        // Check that all models are unique
        var condition = DSL.or(
                modelList.stream().map(this::getMatchCondition)
                         .toList()
        );

        var matches = StreamSupport.stream(retrieveAll(condition).spliterator(), false).toList();

        for (var model : modelList) {
            Map<String, String> modelSpecificErrors = errors.get(model);
            boolean didModelStartWithErrors = modelSpecificErrors != null;

            if (!didModelStartWithErrors) {
                modelSpecificErrors = new HashMap<>();
            }

            // Only check for match if model does not have any errors related to word and part
            if (modelSpecificErrors.containsKey("word") || modelSpecificErrors.containsKey("part")) continue;

            // Check if model has a match
            var foundMatch = matches.stream().filter(match -> match.getWordId()
                                                                   .equals(model.getWordId()) &&
                                            match.getPartId().equals(model.getPartId()))
                                    .findFirst();

            if (foundMatch.isEmpty()) continue;
            modelSpecificErrors.put("_", "WordPart is not unique");

            if (!didModelStartWithErrors) {
                errors.put(model, modelSpecificErrors);
            }
        }

        return errors;
    }

}
